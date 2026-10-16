import re
from typing import List

CONTRACTIONS = {
    "can't": "cannot",
    "won't": "will not",
    "n't": " not",
    "'re": " are",
    "'s": " is",
    "'d": " would",
    "'ll": " will",
    "'t": " not",
    "'ve": " have",
    "'m": " am",
    "it's": "it is",
    "don't": "do not",
    "doesn't": "does not",
    "didn't": "did not"
}

NEGATION_TOKENS = {
    "not", "no", "never", "hardly", "barely", "scarcely", "without",
    "cannot", "cant", "dont", "doesnt", "didnt", "isnt", "arent", "wasnt", "werent"
}

def clean_text(raw_text: str) -> str:
    """Normalize whitespace, lowercase text, expand contractions, and preserve negations."""
    if not isinstance(raw_text, str) or not raw_text.strip():
        return ""

    text = raw_text.strip().lower()

    # Expand common contractions
    for contraction, expansion in CONTRACTIONS.items():
        text = text.replace(contraction, expansion)

    # Replace punctuation with spaces while keeping alphanumeric characters
    text = re.sub(r"[^a-z0-9\s]", " ", text)

    # Normalize multiple whitespace
    tokens = [w for w in text.split() if w]
    return " ".join(tokens)

def handle_negation(text: str, window: int = 2) -> str:
    """Attach a NEG_ prefix to tokens following negation words within a window."""
    words = text.split()
    processed_words = []
    negation_counter = 0

    for word in words:
        if word in NEGATION_TOKENS:
            negation_counter = window
            processed_words.append(word)
        elif negation_counter > 0:
            processed_words.append(f"NEG_{word}")
            negation_counter -= 1
        else:
            processed_words.append(word)

    return " ".join(processed_words)

def preprocess_pipeline(raw_text: str) -> str:
    cleaned = clean_text(raw_text)
    return handle_negation(cleaned)
