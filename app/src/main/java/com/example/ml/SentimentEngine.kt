package com.example.ml

import java.util.Locale
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

object SentimentEngine {

    const val MODEL_VERSION = "v1.3-pro-hybrid-nlp"

    // Negation terms that invert subsequent word polarities
    private val NEGATION_WORDS = setOf(
        "not", "never", "no", "hardly", "barely", "scarcely", "without",
        "neither", "nor", "cannot", "cant", "wont", "dont", "doesnt",
        "didnt", "isnt", "arent", "wasnt", "werent", "hasnt", "havent", "hadnt",
        "nahi", "mat", "na"
    )

    // Contrastive conjunctions where the clause after "but" carries higher weight
    private val CONTRAST_WORDS = setOf("but", "however", "although", "yet", "except", "lekin", "magar", "par")

    // Intensifiers and diminishers
    private val INTENSIFIERS = mapOf(
        "very" to 1.5f,
        "extremely" to 1.8f,
        "super" to 1.6f,
        "highly" to 1.7f,
        "really" to 1.4f,
        "absolutely" to 1.9f,
        "completely" to 1.6f,
        "totally" to 1.5f,
        "bohot" to 1.6f,
        "bahut" to 1.6f,
        "bilkul" to 1.7f,
        "slightly" to 0.5f,
        "hardly" to 0.3f,
        "barely" to 0.3f,
        "somewhat" to 0.7f,
        "fairly" to 0.8f
    )

    // Sarcasm indicators: positive adjectives paired with explicit trouble markers
    private val SARCASM_PATTERNS = listOf(
        Regex("oh\\s+great.*delay", RegexOption.IGNORE_CASE),
        Regex("wow.*brilliant.*(lost|broke|failed|late|missing)", RegexOption.IGNORE_CASE),
        Regex("just\\s+what\\s+i\\s+needed.*(broken|problem|defect|delay)", RegexOption.IGNORE_CASE),
        Regex("thanks\\s+for\\s+nothing", RegexOption.IGNORE_CASE),
        Regex("love\\s+waiting.*hours", RegexOption.IGNORE_CASE)
    )

    // Bigram phrase weights: (Phrase -> Weight)
    private val BIGRAM_LEXICON = mapOf(
        // English Positive
        "highly recommend" to 2.4f,
        "top notch" to 2.3f,
        "well made" to 1.8f,
        "out of this world" to 2.5f,
        "super happy" to 2.2f,
        "game changer" to 2.1f,
        "easy to use" to 1.7f,
        "five stars" to 2.5f,

        // English Negative
        "waste of time" to -2.5f,
        "waste of money" to -2.7f,
        "poor quality" to -2.3f,
        "bad quality" to -2.2f,
        "never again" to -2.6f,
        "fell apart" to -2.4f,
        "worst ever" to -2.8f,
        "dont buy" to -2.5f,
        "do not buy" to -2.6f,
        "stop working" to -2.2f,
        "stopped working" to -2.4f,
        "not good" to -1.9f,
        "not bad" to 0.9f,
        "not great" to -1.2f,
        "not worth" to -2.1f,

        // Hinglish & Multilingual terms
        "bohot accha" to 2.2f,
        "bahut badiya" to 2.4f,
        "shandar experience" to 2.3f,
        "mast hai" to 2.0f,
        "zabardast product" to 2.4f,
        "bilkul bakwas" to -2.6f,
        "khatarnaak service" to -2.2f,
        "paisa barbad" to -2.7f,
        "kharab product" to -2.3f,
        "mat khareedo" to -2.5f,

        // Neutral anchors
        "as expected" to 0.1f,
        "arrived yesterday" to 0.0f,
        "arrived today" to 0.0f,
        "package arrived" to 0.0f,
        "standard size" to 0.0f
    )

    // Unigram Lexicon with calibrated sentiment weights
    private val UNIGRAM_LEXICON = mapOf(
        // Strong positive
        "amazing" to 2.4f,
        "excellent" to 2.5f,
        "fantastic" to 2.3f,
        "wonderful" to 2.2f,
        "awesome" to 2.3f,
        "superb" to 2.4f,
        "flawless" to 2.4f,
        "love" to 2.1f,
        "loved" to 2.0f,
        "loves" to 2.0f,
        "best" to 2.2f,
        "great" to 1.8f,
        "perfect" to 2.3f,
        "delicious" to 2.0f,
        "outstanding" to 2.4f,
        "brilliant" to 2.2f,
        "impressive" to 1.9f,
        "exceptional" to 2.3f,
        "masterpiece" to 2.5f,
        "badiya" to 2.0f,
        "badhiya" to 2.0f,
        "shandar" to 2.2f,
        "zabardast" to 2.3f,
        "mast" to 1.9f,
        "accha" to 1.4f,
        "achha" to 1.4f,

        // Moderate positive
        "good" to 1.2f,
        "nice" to 1.1f,
        "happy" to 1.4f,
        "pleased" to 1.3f,
        "satisfied" to 1.5f,
        "recommend" to 1.6f,
        "enjoyed" to 1.4f,
        "smooth" to 1.2f,
        "fast" to 1.1f,
        "reliable" to 1.5f,
        "helpful" to 1.4f,
        "friendly" to 1.3f,
        "polite" to 1.2f,
        "clean" to 1.1f,
        "worth" to 1.2f,
        "durable" to 1.4f,
        "decent" to 0.8f,
        "fine" to 0.6f,
        "solid" to 0.9f,

        // Strong negative
        "terrible" to -2.5f,
        "horrible" to -2.5f,
        "awful" to -2.4f,
        "worst" to -2.7f,
        "disgusting" to -2.6f,
        "pathetic" to -2.4f,
        "scam" to -2.7f,
        "garbage" to -2.5f,
        "trash" to -2.4f,
        "hate" to -2.2f,
        "hated" to -2.2f,
        "useless" to -2.3f,
        "worthless" to -2.5f,
        "disastrous" to -2.5f,
        "defective" to -2.3f,
        "bakwas" to -2.5f,
        "bakwaas" to -2.5f,
        "bekaar" to -2.4f,
        "ghatiya" to -2.6f,
        "faltu" to -2.1f,
        "dhokha" to -2.5f,

        // Moderate negative
        "bad" to -1.5f,
        "poor" to -1.6f,
        "disappointing" to -1.8f,
        "disappointed" to -1.7f,
        "slow" to -1.2f,
        "delayed" to -1.3f,
        "broken" to -1.8f,
        "defect" to -1.7f,
        "unhelpful" to -1.5f,
        "rude" to -1.6f,
        "annoying" to -1.4f,
        "frustrating" to -1.6f,
        "waste" to -1.9f,
        "crashed" to -1.6f,
        "failed" to -1.5f,
        "regret" to -1.7f,
        "kharab" to -1.8f,
        "ganda" to -1.7f,
        "bura" to -1.6f,

        // Neutral anchor words
        "arrived" to 0.0f,
        "received" to 0.0f,
        "delivered" to 0.0f,
        "ordered" to 0.0f,
        "package" to 0.0f,
        "box" to 0.0f,
        "item" to 0.0f,
        "product" to 0.0f,
        "today" to 0.0f,
        "yesterday" to 0.0f,
        "standard" to 0.0f,
        "normal" to 0.0f,
        "average" to 0.0f,
        "moderate" to 0.0f,
        "specification" to 0.0f,
        "dimensions" to 0.0f,
        "ok" to 0.1f,
        "okay" to 0.1f
    )

    private val CONTRACTIONS = mapOf(
        "can't" to "cannot",
        "won't" to "will not",
        "n't" to " not",
        "'ve" to " have",
        "'re" to " are",
        "'m" to " am",
        "'ll" to " will",
        "'d" to " would",
        "it's" to "it is"
    )

    fun analyze(rawText: String): SentimentResult {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty()) {
            return SentimentResult(
                sentiment = SentimentType.NEUTRAL,
                confidence = 0.50f,
                positiveProb = 0.33f,
                negativeProb = 0.33f,
                neutralProb = 0.34f,
                detectedKeywords = emptyList(),
                polarityScore = 0.0f,
                subjectivityScore = 0.0f,
                explanation = "Empty input text. Defaulted to neutral classification."
            )
        }

        // Check for sarcasm pattern
        for (pattern in SARCASM_PATTERNS) {
            if (pattern.containsMatchIn(trimmed)) {
                return SentimentResult(
                    sentiment = SentimentType.NEGATIVE,
                    confidence = 0.84f,
                    positiveProb = 0.08f,
                    negativeProb = 0.84f,
                    neutralProb = 0.08f,
                    detectedKeywords = listOf(KeywordToken("sarcasm_detected", -2.5f, false)),
                    polarityScore = -0.76f,
                    subjectivityScore = 0.9f,
                    explanation = "Sarcastic phrase pattern identified (inverted affective polarity). Classified as negative."
                )
            }
        }

        // Step 1: Normalize & Expand contractions
        var normalized = trimmed.lowercase(Locale.ROOT)
        for ((k, v) in CONTRACTIONS) {
            normalized = normalized.replace(k, v)
        }

        // Step 2: Multi-word Bigram check
        val detectedTokens = mutableListOf<KeywordToken>()
        var bigramScoreSum = 0.0f
        var bigramMatches = 0

        for ((phrase, weight) in BIGRAM_LEXICON) {
            if (normalized.contains(phrase)) {
                bigramScoreSum += weight
                bigramMatches++
                detectedTokens.add(
                    KeywordToken(
                        word = phrase,
                        polarity = weight,
                        isNegated = false
                    )
                )
            }
        }

        // Step 3: Tokenize & Negation Scope Tracking
        val words = normalized
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        var unigramScoreSum = 0.0f
        var sentimentWordCount = 0
        var negationWindow = 0
        var currentIntensifier = 1.0f
        var postContrastMultiplier = 1.0f

        var positiveFoundCount = 0
        var negativeFoundCount = 0

        for (w in words) {
            if (w in CONTRAST_WORDS) {
                // Statements after 'but' carry more perceptual weight in customer reviews
                postContrastMultiplier = 1.4f
                continue
            }

            if (w in NEGATION_WORDS) {
                negationWindow = 3
                continue
            }

            if (w in INTENSIFIERS) {
                currentIntensifier = INTENSIFIERS[w] ?: 1.0f
                continue
            }

            val baseWeight = UNIGRAM_LEXICON[w]
            if (baseWeight != null) {
                val isNegated = negationWindow > 0
                val finalWeight = if (isNegated) {
                    if (baseWeight > 0) -1.3f * baseWeight else 0.8f * -baseWeight
                } else {
                    baseWeight * currentIntensifier * postContrastMultiplier
                }

                if (baseWeight != 0.0f) {
                    unigramScoreSum += finalWeight
                    sentimentWordCount++
                    if (finalWeight > 0) positiveFoundCount++ else negativeFoundCount++

                    detectedTokens.add(
                        KeywordToken(
                            word = if (isNegated) "not $w" else w,
                            polarity = finalWeight,
                            isNegated = isNegated
                        )
                    )
                }

                currentIntensifier = 1.0f
            }

            if (negationWindow > 0) {
                negationWindow--
            }
        }

        val totalScore = bigramScoreSum + unigramScoreSum
        val totalKeywords = bigramMatches + sentimentWordCount

        // Step 4: Calibrated Logits
        val posLogit: Float
        val negLogit: Float
        val neuLogit: Float

        val isMixed = positiveFoundCount > 0 && negativeFoundCount > 0 && Math.abs(totalScore) < 1.0f

        if (totalKeywords == 0) {
            // Factual / Neutral statement
            posLogit = 0.2f
            negLogit = 0.2f
            neuLogit = 2.4f
        } else if (isMixed) {
            // Mixed sentiment statement (e.g. "great camera but terrible battery")
            posLogit = 1.2f
            negLogit = 1.2f
            neuLogit = 2.0f // Neutral / balanced classification for mixed reviews
        } else {
            if (totalScore > 0.4f) {
                posLogit = 1.2f + (totalScore * 0.9f)
                negLogit = -0.5f - (totalScore * 0.4f)
                neuLogit = 0.4f
            } else if (totalScore < -0.4f) {
                posLogit = -0.5f - (-totalScore * 0.4f)
                negLogit = 1.2f + (-totalScore * 0.9f)
                neuLogit = 0.4f
            } else {
                posLogit = 0.8f + totalScore
                negLogit = 0.8f - totalScore
                neuLogit = 1.8f
            }
        }

        // Softmax
        val maxLogit = max(posLogit, max(negLogit, neuLogit))
        val expPos = exp((posLogit - maxLogit).toDouble()).toFloat()
        val expNeg = exp((negLogit - maxLogit).toDouble()).toFloat()
        val expNeu = exp((neuLogit - maxLogit).toDouble()).toFloat()
        val sumExp = expPos + expNeg + expNeu

        val posProb = (expPos / sumExp)
        val negProb = (expNeg / sumExp)
        val neuProb = (expNeu / sumExp)

        val predictedSentiment: SentimentType
        val confidence: Float

        if (posProb >= negProb && posProb >= neuProb) {
            predictedSentiment = SentimentType.POSITIVE
            confidence = posProb
        } else if (negProb >= posProb && negProb >= neuProb) {
            predictedSentiment = SentimentType.NEGATIVE
            confidence = negProb
        } else {
            predictedSentiment = SentimentType.NEUTRAL
            confidence = neuProb
        }

        val clampedPolarity = (posProb - negProb).coerceIn(-1.0f, 1.0f)
        val subjectivity = if (words.isEmpty()) 0.0f else {
            min(1.0f, (totalKeywords.toFloat() / max(1, words.size).toFloat()) * 2.2f)
        }

        val explanation = when {
            isMixed -> "Mixed sentiment review containing both positive and critical elements. Classified as balanced/neutral."
            predictedSentiment == SentimentType.POSITIVE -> {
                val cues = detectedTokens.filter { it.polarity > 0 }.map { "'${it.word}'" }
                if (cues.isNotEmpty()) "Positive sentiment with ${(confidence * 100).toInt()}% confidence. Cues: ${cues.take(3).joinToString(", ")}."
                else "Positive language patterns detected."
            }
            predictedSentiment == SentimentType.NEGATIVE -> {
                val cues = detectedTokens.filter { it.polarity < 0 }.map { "'${it.word}'" }
                if (cues.isNotEmpty()) "Negative sentiment with ${(confidence * 100).toInt()}% confidence. Critical cues: ${cues.take(3).joinToString(", ")}."
                else "Dissatisfied or critical sentiment detected."
            }
            else -> "Objective, balanced, or informational tone without prominent polar emotion."
        }

        return SentimentResult(
            sentiment = predictedSentiment,
            confidence = confidence,
            positiveProb = posProb,
            negativeProb = negProb,
            neutralProb = neuProb,
            detectedKeywords = detectedTokens,
            polarityScore = clampedPolarity,
            subjectivityScore = subjectivity,
            explanation = explanation,
            modelVersion = MODEL_VERSION
        )
    }
}
