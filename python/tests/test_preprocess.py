import pytest
from src.preprocess import clean_text, handle_negation, preprocess_pipeline

def test_clean_text_basic():
    assert clean_text("  The Movie was AMAZING!  ") == "the movie was amazing"

def test_contraction_expansion():
    cleaned = clean_text("I didn't like this, it's bad")
    assert "did not" in cleaned
    assert "it is" in cleaned

def test_negation_tagging():
    processed = handle_negation("this product is not good")
    assert "NEG_good" in processed

def test_empty_string():
    assert clean_text("") == ""
    assert preprocess_pipeline("   ") == ""
