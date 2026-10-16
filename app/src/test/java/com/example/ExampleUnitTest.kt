package com.example

import com.example.ml.SentimentEngine
import com.example.ml.SentimentType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SentimentEngineTest {

    @Test
    fun testPositiveSentiment() {
        val result = SentimentEngine.analyze("The movie was amazing! Outstanding acting and visuals.")
        assertEquals(SentimentType.POSITIVE, result.sentiment)
        assertTrue("Confidence should be >= 0.6", result.confidence >= 0.6f)
        assertTrue("Positive prob should exceed negative prob", result.positiveProb > result.negativeProb)
    }

    @Test
    fun testNegativeSentiment() {
        val result = SentimentEngine.analyze("The product quality was terrible. Total waste of money.")
        assertEquals(SentimentType.NEGATIVE, result.sentiment)
        assertTrue("Confidence should be >= 0.6", result.confidence >= 0.6f)
        assertTrue("Negative prob should exceed positive prob", result.negativeProb > result.positiveProb)
    }

    @Test
    fun testNeutralSentiment() {
        val result = SentimentEngine.analyze("The product arrived yesterday.")
        assertEquals(SentimentType.NEUTRAL, result.sentiment)
        assertTrue("Neutral prob should be highest", result.neutralProb >= result.positiveProb)
    }

    @Test
    fun testNegationHandling() {
        // "not good" must be classified as negative, NOT positive
        val result = SentimentEngine.analyze("The battery life is not good at all.")
        assertEquals(SentimentType.NEGATIVE, result.sentiment)
    }

    @Test
    fun testSarcasmDetection() {
        val result = SentimentEngine.analyze("Oh great, another delay!")
        assertEquals(SentimentType.NEGATIVE, result.sentiment)
    }

    @Test
    fun testHinglishSentiment() {
        val resPos = SentimentEngine.analyze("Bohot accha product hai, shandar experience!")
        assertEquals(SentimentType.POSITIVE, resPos.sentiment)

        val resNeg = SentimentEngine.analyze("Bilkul bakwas service, paisa barbad!")
        assertEquals(SentimentType.NEGATIVE, resNeg.sentiment)
    }

    @Test
    fun testProbabilitiesSumToOne() {
        val result = SentimentEngine.analyze("Customer support was polite and resolved the issue quickly.")
        val sum = result.positiveProb + result.negativeProb + result.neutralProb
        assertTrue("Probabilities should sum to approximately 1.0", Math.abs(sum - 1.0f) < 0.01f)
    }

    @Test
    fun testEmptyInputHandling() {
        val result = SentimentEngine.analyze("   ")
        assertEquals(SentimentType.NEUTRAL, result.sentiment)
    }
}
