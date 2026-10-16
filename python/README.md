# Sentilyzer: Python ML & FastAPI Backend

This directory contains the machine learning training pipeline and FastAPI REST API backend for the **Sentiment Analysis Tool**.

---

## 1. Directory Structure

```text
python/
├── app.py                # FastAPI server application
├── config.py             # Configuration parameters & paths
├── requirements.txt      # Python dependencies
├── src/
│   ├── data_loader.py    # Dataset loading & synthetic generator
│   ├── preprocess.py     # Text normalization & negation handling
│   ├── train.py          # TF-IDF + Logistic Regression training
│   └── predict.py        # Single & batch inference functions
├── tests/
│   └── test_preprocess.py# Preprocessing test suite
└── README.md
```

---

## 2. Installation & Quick Start

### Step 1: Create a virtual environment
```bash
python -m venv .venv
source .venv/bin/activate    # On Linux/macOS
.venv\Scripts\activate       # On Windows
```

### Step 2: Install dependencies
```bash
pip install -r requirements.txt
```

### Step 3: Train the Sentiment Model
```bash
python -m src.train
```

### Step 4: Run the FastAPI Server
```bash
uvicorn app:app --reload --host 0.0.0.0 --port 8000
```

Open interactive Swagger documentation in your browser:
- `http://localhost:8000/docs`

---

## 3. API Contract

### Predict Sentiment:
- **POST** `/analyze`
- Request:
```json
{
  "text": "The movie was amazing!"
}
```
- Response:
```json
{
  "id": 1,
  "text": "The movie was amazing!",
  "sentiment": "positive",
  "confidence": 0.892,
  "positive_prob": 0.892,
  "negative_prob": 0.045,
  "neutral_prob": 0.063,
  "model_version": "v1.2-tfidf-logreg",
  "created_at": "2026-10-16T14:30:00"
}
```
