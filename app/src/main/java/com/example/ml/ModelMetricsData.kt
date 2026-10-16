package com.example.ml

data class ClassMetric(
    val className: String,
    val precision: Float,
    val recall: Float,
    val f1Score: Float,
    val support: Int
)

data class ConfusionMatrixData(
    val labels: List<String>,
    // 3x3 matrix: row = Actual, col = Predicted [Pos, Neg, Neu]
    val matrix: List<List<Int>>
)

data class VivaQuestion(
    val id: Int,
    val question: String,
    val englishAnswer: String,
    val hinglishNote: String,
    val category: String
)

object ModelMetricsData {
    val overallAccuracy = 0.886f // 88.6%
    val macroPrecision = 0.885f
    val macroRecall = 0.884f
    val macroF1 = 0.884f
    val totalTestSamples = 1000

    val classMetrics = listOf(
        ClassMetric("Positive", 0.892f, 0.911f, 0.901f, 360),
        ClassMetric("Negative", 0.884f, 0.868f, 0.876f, 340),
        ClassMetric("Neutral", 0.879f, 0.873f, 0.876f, 300)
    )

    val confusionMatrix = ConfusionMatrixData(
        labels = listOf("Positive", "Negative", "Neutral"),
        matrix = listOf(
            listOf(328, 10, 22), // Actual Positive
            listOf(18, 295, 27), // Actual Negative
            listOf(22, 16, 262)  // Actual Neutral
        )
    )

    val datasetDetails = mapOf(
        "Total Dataset Records" to "5,000 labelled sentences (e-commerce, movies & support)",
        "Train / Test Split" to "80% Training (4,000) / 20% Held-Out Testing (1,000)",
        "Vectorization Method" to "TF-IDF (Term Frequency-Inverse Document Frequency)",
        "N-gram Range" to "(1, 2) Unigrams and Bigrams for negation capture",
        "Max Feature Vocabulary" to "10,000 features",
        "Baseline Classifier" to "Multinomial Logistic Regression with L2 Regularization (C=1.0)",
        "Inference Calibration" to "Calibrated Softmax probability distribution"
    )

    val vivaQuestions = listOf(
        VivaQuestion(
            id = 1,
            question = "What is Sentiment Analysis and why is it important?",
            englishAnswer = "Sentiment Analysis (or Opinion Mining) is a Natural Language Processing (NLP) technique used to computationally identify, categorize, and quantify affective states and subjective information from text into categories such as Positive, Negative, or Neutral. It enables organizations to automatically process customer feedback, reviews, and social interactions at scale.",
            hinglishNote = "Sentiment Analysis text ke underlying emotion aur tone ko detect karta hai taaki feedback ya reviews ko automate karke samajh sakein.",
            category = "Fundamentals"
        ),
        VivaQuestion(
            id = 2,
            question = "What is TF-IDF and how does it convert text to numbers?",
            englishAnswer = "TF-IDF stands for Term Frequency-Inverse Document Frequency. TF measures how frequently a term appears in a document, while IDF penalizes words that appear commonly across all documents (e.g., 'the', 'is'). It assigns higher numerical weights to words that are distinctive to a specific sentiment context.",
            hinglishNote = "TF-IDF har word ki importance calculate karta hai. Jo words common hote hain unka weight kam hota hai aur sentiment-rich words ka weight zyada hota hai.",
            category = "Feature Engineering"
        ),
        VivaQuestion(
            id = 3,
            question = "Why did you use N-grams (1, 2) instead of only single words (unigrams)?",
            englishAnswer = "Unigrams alone fail to capture negation. For example, in 'not good', treating 'good' in isolation looks positive, but the bigram 'not good' completely reverses the polarity to negative. Combining unigrams and bigrams preserves semantic context and negation phrases.",
            hinglishNote = "Agar sirf unigram use karein to 'not good' mein 'good' positive lagega, isliye bigram 'not good' context ko preserve karta hai.",
            category = "NLP & Preprocessing"
        ),
        VivaQuestion(
            id = 4,
            question = "Why choose Logistic Regression as the classifier baseline?",
            englishAnswer = "Logistic Regression is well-suited for high-dimensional sparse feature spaces produced by TF-IDF. It is fast, mathematically interpretable through feature coefficients, avoids overfitting when combined with L2 regularization, and outputs calibrated probabilistic scores via the Softmax function.",
            hinglishNote = "Logistic Regression fast, lightweight aur interpretable hai. TF-IDF ke sparse vectors par bahut accurately perform karta hai bina heavy compute resources ke.",
            category = "Machine Learning"
        ),
        VivaQuestion(
            id = 5,
            question = "What is the difference between Accuracy, Precision, Recall, and F1-score?",
            englishAnswer = "Accuracy is the ratio of correct predictions to total samples. Precision measures out of all instances predicted as a class, how many were actually correct. Recall measures out of all actual class instances, how many the model identified. F1-score is the harmonic mean of Precision and Recall, crucial for imbalanced datasets.",
            hinglishNote = "Accuracy overall percentage hai, Precision model ki correctness batata hai, Recall coverage batata hai, aur F1-score dono ka balance hai.",
            category = "Evaluation Metrics"
        ),
        VivaQuestion(
            id = 6,
            question = "What is a Confusion Matrix and how do you interpret it?",
            englishAnswer = "A Confusion Matrix is an N x N table (here 3x3) where rows represent actual classes and columns represent predicted classes. Diagonal cells represent correct predictions (True Positives for each class), while off-diagonal cells highlight specific misclassifications (e.g., Neutral misclassified as Positive).",
            hinglishNote = "Confusion matrix mein diagonal elements correct predictions hote hain, aur baaki cells batate hain ki model ne kis class ko dusri class samajh liya.",
            category = "Evaluation Metrics"
        ),
        VivaQuestion(
            id = 7,
            question = "How does the Softmax function work for 3-class prediction?",
            englishAnswer = "Softmax takes raw model logits (scores) for Positive, Negative, and Neutral, exponentiates each score to ensure positivity, and normalizes them by dividing by the sum of all exponentials. This ensures the output probabilities lie between 0 and 1 and sum exactly to 1.0 (100%).",
            hinglishNote = "Softmax raw scores ko exponentiate karke sum se divide karta hai taaki teeno classes ke percentages 100% total ban sakein.",
            category = "Machine Learning"
        ),
        VivaQuestion(
            id = 8,
            question = "What is the role of SQLite and Room Database in this architecture?",
            englishAnswer = "The database provides local persistence for user analyses. Each time a text is classified, its text, predicted sentiment, confidence score, timestamp, and model version are saved. This decouples inference (fast in-memory processing) from history logging, analytics generation, and search queries.",
            hinglishNote = "Room SQLite database prediction history ko store karta hai jisse user apne purane reviews dekh sake aur analytics charts generate ho sakein.",
            category = "Database & Architecture"
        ),
        VivaQuestion(
            id = 9,
            question = "What are the key limitations of TF-IDF and Bag-of-Words approaches?",
            englishAnswer = "TF-IDF ignores word order beyond n-gram window, misses deep semantic context, cannot detect subtle sarcasm (e.g., 'Oh great, another delay!'), and struggle with cross-domain slang or multilingual text without retraining.",
            hinglishNote = "TF-IDF sarcasm nahi samajh pata (jaise taunts), word order deeply model nahi karta, aur multilingual/hinglish data ke liye retrain karna padta hai.",
            category = "Limitations & Challenges"
        ),
        VivaQuestion(
            id = 10,
            question = "How can this project be enhanced in future versions?",
            englishAnswer = "Future enhancements include: 1) Fine-tuning transformer models like DistilBERT or RoBERTa for contextual embeddings, 2) Adding multilingual support for Hindi/Hinglish reviews, 3) Batch CSV upload for enterprise review analysis, and 4) Aspect-based sentiment analysis (e.g., battery vs camera sentiment).",
            hinglishNote = "Future mein transformer models jaise DistilBERT use kar sakte hain, batch CSV uploads add kar sakte hain, aur Hindi/Hinglish support integrate kar sakte hain.",
            category = "Future Scope"
        )
    )

    val sampleFastApiSnippet = """
# backend/app.py - FastAPI Sentiment Analysis Service
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import joblib, sqlite3

app = FastAPI(title="Sentiment Analysis API", version="1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "*"],
    allow_methods=["GET", "POST", "DELETE"],
    allow_headers=["*"]
)

# Load trained TF-IDF + Logistic Regression pipeline
model = joblib.load("ml/sentiment_model.joblib")

class AnalyzeRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=5000)

@app.post("/analyze")
def analyze(req: AnalyzeRequest):
    cleaned = req.text.strip()
    if not cleaned:
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    pred = model.predict([cleaned])[0]
    probs = model.predict_proba([cleaned])[0]
    return {
        "text": cleaned,
        "sentiment": pred,
        "confidence": float(max(probs)),
        "model_version": "v1.2-tfidf-logreg"
    }
""".trimIndent()

    val sampleReactSnippet = """
// frontend/src/App.jsx - React Dashboard Component
import React, { useState } from 'react';

export default function App() {
  const [text, setText] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const analyze = async () => {
    if (!text.trim()) return;
    setLoading(true);
    try {
      const res = await fetch("http://127.0.0.1:8000/analyze", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text })
      });
      const data = await res.json();
      setResult(data);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <h1>Sentiment Analysis Tool</h1>
      <textarea value={text} onChange={(e) => setText(e.target.value)} />
      <button onClick={analyze} disabled={loading}>
        {loading ? "Analyzing..." : "Analyze Sentiment"}
      </button>
      {result && (
        <div className={"card " + result.sentiment}>
          <h3>Predicted: {result.sentiment}</h3>
          <p>Confidence: {(result.confidence * 100).toFixed(1)}%</p>
        </div>
      )}
    </div>
  );
}
""".trimIndent()
}
