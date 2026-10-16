package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {

    @Query("SELECT * FROM analyses ORDER BY created_at DESC")
    fun getAllAnalyses(): Flow<List<AnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisEntity): Long

    @Query("SELECT * FROM analyses WHERE id = :id LIMIT 1")
    suspend fun getAnalysisById(id: Long): AnalysisEntity?

    @Query("DELETE FROM analyses WHERE id = :id")
    suspend fun deleteAnalysisById(id: Long)

    @Query("DELETE FROM analyses")
    suspend fun deleteAllAnalyses()

    @Query("SELECT * FROM analyses WHERE input_text LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchAnalyses(query: String): Flow<List<AnalysisEntity>>

    @Query("SELECT * FROM analyses WHERE sentiment = :sentiment ORDER BY created_at DESC")
    fun filterBySentiment(sentiment: String): Flow<List<AnalysisEntity>>

    @Query("SELECT COUNT(*) FROM analyses")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analyses WHERE sentiment = :sentiment")
    fun getCountBySentiment(sentiment: String): Flow<Int>
}
