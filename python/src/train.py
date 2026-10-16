import joblib
import json
from pathlib import Path
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix

import config
from src.preprocess import preprocess_pipeline
from src.data_loader import load_and_validate_dataset

def train_model():
    """Train TF-IDF + Logistic Regression pipeline and save model artifact."""
    print("Loading dataset...")
    df = load_and_validate_dataset(config.RAW_DATA_PATH)
    print(f"Loaded {len(df)} records across classes: {df['sentiment'].value_counts().to_dict()}")

    # Apply text preprocessing
    X = [preprocess_pipeline(t) for t in df["text"]]
    y = df["sentiment"].tolist()

    # Train-test split
    X_train, X_test, y_train, y_test = train_test_split(
        X, y,
        test_size=config.TEST_SIZE,
        random_state=config.RANDOM_STATE,
        stratify=y if len(set(y)) == 3 else None
    )

    # Scikit-learn Pipeline
    pipeline = Pipeline([
        ("tfidf", TfidfVectorizer(
            ngram_range=config.TFIDF_NGRAM_RANGE,
            max_features=config.TFIDF_MAX_FEATURES,
            lowercase=False
        )),
        ("classifier", LogisticRegression(
            max_iter=config.LOGREG_MAX_ITER,
            C=config.LOGREG_C,
            random_state=config.RANDOM_STATE
        ))
    ])

    print("Fitting pipeline...")
    pipeline.fit(X_train, y_train)

    # Evaluation
    y_pred = pipeline.predict(X_test)
    acc = accuracy_score(y_test, y_pred)
    report = classification_report(y_test, y_pred, output_dict=True, zero_division=0)
    cm = confusion_matrix(y_test, y_pred, labels=["positive", "negative", "neutral"]).tolist()

    print(f"Model Accuracy on Held-Out Test Data: {acc:.4f}")

    # Save artifacts
    config.MODELS_DIR.mkdir(parents=True, exist_ok=True)
    joblib.dump(pipeline, config.MODEL_PATH)
    print(f"Model saved to {config.MODEL_PATH}")

    config.METRICS_PATH.parent.mkdir(parents=True, exist_ok=True)
    eval_data = {
        "model_version": config.MODEL_VERSION,
        "accuracy": acc,
        "classification_report": report,
        "confusion_matrix": cm,
        "labels": ["positive", "negative", "neutral"],
        "train_samples": len(X_train),
        "test_samples": len(X_test)
    }
    with open(config.METRICS_PATH, "w") as f:
        json.dump(eval_data, f, indent=2)
    print(f"Metrics saved to {config.METRICS_PATH}")

    return pipeline, eval_data

if __name__ == "__main__":
    train_model()
