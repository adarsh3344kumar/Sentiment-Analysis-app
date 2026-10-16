import os
import pandas as pd
from pathlib import Path
from typing import Tuple

VALID_LABELS = {"positive", "negative", "neutral"}

# High-quality benchmark synthetic dataset for immediate smoke-testing & training
DEMO_DATASET = [
    ("The movie was amazing and absolutely brilliant!", "positive"),
    ("Great performance and wonderful cinematography.", "positive"),
    ("Excellent product quality, highly recommend!", "positive"),
    ("Super fast delivery and fantastic packaging.", "positive"),
    ("Loved the experience, very pleased and satisfied.", "positive"),
    ("Solid build quality, works like a charm.", "positive"),
    ("The customer service was polite, helpful, and courteous.", "positive"),
    ("Delicious food, outstanding service, five stars!", "positive"),
    ("The product quality was terrible, total waste of money.", "negative"),
    ("Stopped working after two days, horrible experience.", "negative"),
    ("Worst customer support ever, rude and unhelpful.", "negative"),
    ("Very disappointing performance, defect in hardware.", "negative"),
    ("Battery life is not good at all, draining fast.", "negative"),
    ("Do not buy, absolute garbage and scam.", "negative"),
    ("Delayed shipment and the item arrived damaged.", "negative"),
    ("Fails to connect to bluetooth, complete nightmare.", "negative"),
    ("The package arrived yesterday afternoon.", "neutral"),
    ("The product dimensions and weight are standard.", "neutral"),
    ("Received the package in a cardboard box.", "neutral"),
    ("Ordered on Monday, delivered on Wednesday.", "neutral"),
    ("The device has 4GB RAM and 64GB storage capacity.", "neutral"),
    ("The color is navy blue with silver edges.", "neutral"),
    ("I received this item as a replacement unit.", "neutral"),
    ("Contains power adapter, warranty card, and manual.", "neutral")
]

def generate_sample_dataset(output_path: Path):
    """Generate a clean benchmark dataset for demonstration and local training."""
    output_path.parent.mkdir(parents=True, exist_ok=True)
    df = pd.DataFrame(DEMO_DATASET, columns=["text", "sentiment"])
    df.to_csv(output_path, index=False)
    return df

def load_and_validate_dataset(csv_path: Path) -> pd.DataFrame:
    """Load dataset from CSV and validate text and sentiment columns."""
    if not csv_path.exists():
        generate_sample_dataset(csv_path)

    df = pd.read_csv(csv_path)

    if "text" not in df.columns or "sentiment" not in df.columns:
        raise ValueError("Dataset CSV must contain 'text' and 'sentiment' columns.")

    # Drop missing and duplicate rows
    df = df.dropna(subset=["text", "sentiment"])
    df["sentiment"] = df["sentiment"].astype(str).str.strip().str.lower()
    df = df[df["sentiment"].isin(VALID_LABELS)]
    df = df.drop_duplicates(subset=["text"])

    return df
