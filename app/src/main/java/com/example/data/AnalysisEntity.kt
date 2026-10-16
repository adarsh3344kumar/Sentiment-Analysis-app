package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyses")
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "input_text")
    val inputText: String,

    @ColumnInfo(name = "sentiment")
    val sentiment: String, // "positive", "negative", "neutral"

    @ColumnInfo(name = "confidence")
    val confidence: Float,

    @ColumnInfo(name = "positive_prob")
    val positiveProb: Float = 0f,

    @ColumnInfo(name = "negative_prob")
    val negativeProb: Float = 0f,

    @ColumnInfo(name = "neutral_prob")
    val neutralProb: Float = 0f,

    @ColumnInfo(name = "model_version")
    val modelVersion: String = "v1.2-tfidf-logreg",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "keywords")
    val keywords: String = ""
)
