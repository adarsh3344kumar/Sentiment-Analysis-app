import joblib
from pathlib import Path
from typing import Dict, Any, List
import config
from src.preprocess import preprocess_pipeline

_MODEL_CACHE = None

def get_model():
    global _MODEL_CACHE
    if _MODEL_CACHE is None:
        if not config.MODEL_PATH.exists():
            from src.train import train_model
            _MODEL_CACHE, _ = train_model()
        else:
            _MODEL_CACHE = joblib.load(config.MODEL_PATH)
    return _MODEL_CACHE

def predict_single(text: str) -> Dict[str, Any]:
    """Classify sentiment of a single review text with probabilities."""
    model = get_model()
    processed = preprocess_pipeline(text)
    prediction = model.predict([processed])[0]
    classes = list(model.classes_)
    probs = model.predict_proba([processed])[0]

    prob_dict = {cls: float(prob) for cls, prob in zip(classes, probs)}
    confidence = float(max(probs))

    return {
        "text": text,
        "sentiment": prediction,
        "confidence": confidence,
        "positive_prob": prob_dict.get("positive", 0.0),
        "negative_prob": prob_dict.get("negative", 0.0),
        "neutral_prob": prob_dict.get("neutral", 0.0),
        "model_version": config.MODEL_VERSION
    }

def predict_batch(texts: List[str]) -> List[Dict[str, Any]]:
    """Classify sentiment of multiple texts."""
    return [predict_single(t) for t in texts if isinstance(t, str) and t.strip()]
