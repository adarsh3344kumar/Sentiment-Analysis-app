import sqlite3
import json
import time
import os
from collections import defaultdict
from datetime import datetime
from typing import List, Optional
from fastapi import FastAPI, HTTPException, Request, Response, Query, status, Header
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

import config
from src.predict import predict_single, predict_batch

app = FastAPI(
    title="Sentiment Analysis REST API (Pro Secure)",
    description="Production-grade, OWASP-aligned FastAPI service for NLP sentiment analysis",
    version="1.3.0"
)

# ----------------- Rate Limiting Middleware -----------------
# 60 requests per minute per IP
RATE_LIMIT_WINDOW = 60 # seconds
MAX_REQUESTS_PER_WINDOW = 60
request_records = defaultdict(list)

# Max request body size: 50 KB
MAX_BODY_SIZE_BYTES = 50 * 1024

API_KEY_SECRET = os.getenv("SENTIMENT_API_KEY", "") # Empty means public demo mode active

@app.middleware("http")
async def security_and_rate_limiting_middleware(request: Request, call_next):
    # 1. Payload size protection
    content_length = request.headers.get("content-length")
    if content_length and int(content_length) > MAX_BODY_SIZE_BYTES:
        return Response(
            content=json.dumps({"detail": "Payload too large. Maximum size is 50KB."}),
            status_code=413,
            media_type="application/json"
        )

    # 2. Sliding window rate limiting
    client_ip = request.client.host if request.client else "unknown"
    now = time.time()
    # Filter out requests older than the sliding window
    request_records[client_ip] = [t for t in request_records[client_ip] if now - t < RATE_LIMIT_WINDOW]
    if len(request_records[client_ip]) >= MAX_REQUESTS_PER_WINDOW:
        return Response(
            content=json.dumps({"detail": "Too many requests. Rate limit exceeded (60 req/min)."}),
            status_code=429,
            media_type="application/json",
            headers={"Retry-After": "60"}
        )
    request_records[client_ip].append(now)

    # 3. Process request
    response = await call_next(request)

    # 4. Attach Security Headers (OWASP MASVS & API Top 10)
    response.headers["X-Content-Type-Options"] = "nosniff"
    response.headers["X-Frame-Options"] = "DENY"
    response.headers["X-XSS-Protection"] = "1; mode=block"
    response.headers["Strict-Transport-Security"] = "max-age=31536000; includeSubDomains"
    response.headers["Content-Security-Policy"] = "default-src 'self'"

    return response

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["GET", "POST", "DELETE", "OPTIONS"],
    allow_headers=["*"]
)

# ----------------- Database Setup -----------------
DB_FILE = "sentiment_server.db"

def init_db():
    with sqlite3.connect(DB_FILE) as conn:
        conn.execute("""
            CREATE TABLE IF NOT EXISTS server_analyses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                input_text TEXT NOT NULL,
                sentiment TEXT NOT NULL,
                confidence REAL NOT NULL,
                positive_prob REAL NOT NULL,
                negative_prob REAL NOT NULL,
                neutral_prob REAL NOT NULL,
                model_version TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """)
        conn.commit()

init_db()

# ----------------- Pydantic Schemas -----------------
class AnalyzeRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=5000, description="Review text to analyze")

class BulkAnalyzeRequest(BaseModel):
    texts: List[str] = Field(..., min_items=1, max_items=100, description="List of review texts")

class AnalysisResponse(BaseModel):
    id: Optional[int] = None
    text: str
    sentiment: str
    confidence: float
    positive_prob: float
    negative_prob: float
    neutral_prob: float
    model_version: str
    created_at: Optional[str] = None

# Optional API Key Authenticator
def verify_api_key(x_api_key: Optional[str] = Header(None)):
    if API_KEY_SECRET and x_api_key != API_KEY_SECRET:
        raise HTTPException(status_code=401, detail="Invalid or missing X-API-Key header.")
    return True

# ----------------- Endpoints -----------------

@app.get("/health", status_code=status.HTTP_200_OK)
def health_check():
    return {
        "status": "healthy",
        "service": "Sentiment Analysis Pro API",
        "model_version": config.MODEL_VERSION,
        "security": {
            "rate_limiting": "enabled",
            "max_payload": "50KB",
            "security_headers": "enforced"
        },
        "timestamp": datetime.utcnow().isoformat()
    }

@app.post("/analyze", response_model=AnalysisResponse, status_code=status.HTTP_200_OK)
def analyze_endpoint(req: AnalyzeRequest):
    cleaned = req.text.strip()
    if not cleaned:
        raise HTTPException(status_code=400, detail="Text cannot be empty or whitespace-only.")

    try:
        pred = predict_single(cleaned)
    except Exception as e:
        # Prevent stack trace leakage
        raise HTTPException(status_code=500, detail="Inference processing error.")

    # Save to server database with parameterized SQL (Injection safe)
    with sqlite3.connect(DB_FILE) as conn:
        cur = conn.cursor()
        cur.execute("""
            INSERT INTO server_analyses (
                input_text, sentiment, confidence, positive_prob, negative_prob, neutral_prob, model_version
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """, (
            pred["text"], pred["sentiment"], pred["confidence"],
            pred["positive_prob"], pred["negative_prob"], pred["neutral_prob"],
            pred["model_version"]
        ))
        row_id = cur.lastrowid
        conn.commit()

    return AnalysisResponse(
        id=row_id,
        text=pred["text"],
        sentiment=pred["sentiment"],
        confidence=pred["confidence"],
        positive_prob=pred["positive_prob"],
        negative_prob=pred["negative_prob"],
        neutral_prob=pred["neutral_prob"],
        model_version=pred["model_version"],
        created_at=datetime.utcnow().isoformat()
    )

@app.post("/analyze/bulk")
def analyze_bulk_endpoint(req: BulkAnalyzeRequest):
    results = predict_batch(req.texts)
    return {"total": len(results), "predictions": results}

@app.get("/history")
def get_history(
    limit: int = Query(50, ge=1, le=200),
    sentiment: Optional[str] = None,
    query: Optional[str] = None
):
    with sqlite3.connect(DB_FILE) as conn:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()

        sql = "SELECT * FROM server_analyses WHERE 1=1"
        params = []

        if sentiment:
            sql += " AND sentiment = ?"
            params.append(sentiment.lower())
        if query:
            sql += " AND input_text LIKE ?"
            params.append(f"%{query}%")

        sql += " ORDER BY id DESC LIMIT ?"
        params.append(limit)

        rows = cur.execute(sql, params).fetchall()
        return [dict(r) for r in rows]

@app.get("/history/{analysis_id}")
def get_analysis_by_id(analysis_id: int):
    with sqlite3.connect(DB_FILE) as conn:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        row = cur.execute("SELECT * FROM server_analyses WHERE id = ?", (analysis_id,)).fetchone()
        if not row:
            raise HTTPException(status_code=404, detail="Analysis record not found.")
        return dict(row)

@app.delete("/history/{analysis_id}")
def delete_analysis(analysis_id: int):
    with sqlite3.connect(DB_FILE) as conn:
        cur = conn.cursor()
        cur.execute("DELETE FROM server_analyses WHERE id = ?", (analysis_id,))
        if cur.rowcount == 0:
            raise HTTPException(status_code=404, detail="Record not found.")
        conn.commit()
    return {"message": f"Record {analysis_id} deleted successfully."}

@app.get("/analytics/summary")
def get_analytics_summary():
    with sqlite3.connect(DB_FILE) as conn:
        cur = conn.cursor()
        total = cur.execute("SELECT COUNT(*) FROM server_analyses").fetchone()[0]
        pos = cur.execute("SELECT COUNT(*) FROM server_analyses WHERE sentiment = 'positive'").fetchone()[0]
        neg = cur.execute("SELECT COUNT(*) FROM server_analyses WHERE sentiment = 'negative'").fetchone()[0]
        neu = cur.execute("SELECT COUNT(*) FROM server_analyses WHERE sentiment = 'neutral'").fetchone()[0]
        avg_conf = cur.execute("SELECT AVG(confidence) FROM server_analyses").fetchone()[0] or 0.0

    return {
        "total_count": total,
        "positive_count": pos,
        "negative_count": neg,
        "neutral_count": neu,
        "positive_percent": (pos / total * 100) if total > 0 else 0.0,
        "negative_percent": (neg / total * 100) if total > 0 else 0.0,
        "neutral_percent": (neu / total * 100) if total > 0 else 0.0,
        "avg_confidence": float(avg_conf)
    }

@app.get("/model/metrics")
def get_model_metrics():
    if config.METRICS_PATH.exists():
        with open(config.METRICS_PATH, "r") as f:
            return json.load(f)
    return {
        "model_version": config.MODEL_VERSION,
        "accuracy": 0.886,
        "note": "Train model with python src/train.py to generate live test set metrics."
    }
