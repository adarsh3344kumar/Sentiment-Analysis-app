package com.example.ml

data class KeywordToken(
    val word: String,
    val polarity: Float, // > 0 positive, < 0 negative, 0 neutral
    val isNegated: Boolean = false
)

data class SentimentResult(
    val sentiment: SentimentType,
    val confidence: Float, // 0.0 to 1.0 (e.g. 0.92 for 92%)
    val positiveProb: Float, // e.g. 0.88
    val negativeProb: Float, // e.g. 0.04
    val neutralProb: Float, // e.g. 0.08
    val detectedKeywords: List<KeywordToken>,
    val polarityScore: Float, // -1.0 to 1.0
    val subjectivityScore: Float, // 0.0 (objective) to 1.0 (subjective)
    val explanation: String,
    val modelVersion: String = "v1.2-tfidf-logreg"
)
