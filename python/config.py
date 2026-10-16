import os
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
DATA_DIR = BASE_DIR / "data"
RAW_DATA_PATH = DATA_DIR / "raw" / "sentiment_dataset.csv"
PROCESSED_DATA_PATH = DATA_DIR / "processed" / "cleaned_dataset.csv"

MODELS_DIR = BASE_DIR / "models"
MODEL_PATH = MODELS_DIR / "sentiment_model.joblib"
METRICS_PATH = BASE_DIR / "reports" / "evaluation_metrics.json"

MODEL_VERSION = "v1.2-tfidf-logreg"
RANDOM_STATE = 42
TEST_SIZE = 0.20

# TF-IDF Parameters
TFIDF_MAX_FEATURES = 10000
TFIDF_NGRAM_RANGE = (1, 2)

# Logistic Regression Parameters
LOGREG_MAX_ITER = 1000
LOGREG_C = 1.0

# Database Configuration
DATABASE_URL = "sqlite:///./sentiment_server.db"
