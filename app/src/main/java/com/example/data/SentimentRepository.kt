package com.example.data

import com.example.ml.SentimentResult
import kotlinx.coroutines.flow.Flow

class SentimentRepository(private val dao: AnalysisDao) {

    val allAnalyses: Flow<List<AnalysisEntity>> = dao.getAllAnalyses()

    suspend fun saveAnalysis(text: String, result: SentimentResult): Long {
        val entity = AnalysisEntity(
            inputText = text,
            sentiment = result.sentiment.tag,
            confidence = result.confidence,
            positiveProb = result.positiveProb,
            negativeProb = result.negativeProb,
            neutralProb = result.neutralProb,
            modelVersion = result.modelVersion,
            createdAt = System.currentTimeMillis(),
            keywords = result.detectedKeywords.joinToString(",") { it.word }
        )
        return dao.insertAnalysis(entity)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteAnalysisById(id)
    }

    suspend fun deleteAll() {
        dao.deleteAllAnalyses()
    }

    fun search(query: String): Flow<List<AnalysisEntity>> {
        return dao.searchAnalyses(query)
    }

    fun filterBySentiment(sentiment: String): Flow<List<AnalysisEntity>> {
        return dao.filterBySentiment(sentiment)
    }
}
