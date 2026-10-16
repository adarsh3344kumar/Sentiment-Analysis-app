package com.example.ml

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentNegativeBg
import com.example.ui.theme.SentimentNeutral
import com.example.ui.theme.SentimentNeutralBg
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.SentimentPositiveBg

enum class SentimentType(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val containerColor: Color,
    val tag: String
) {
    POSITIVE(
        title = "Positive",
        icon = Icons.Default.CheckCircle,
        color = SentimentPositive,
        containerColor = SentimentPositiveBg,
        tag = "positive"
    ),
    NEGATIVE(
        title = "Negative",
        icon = Icons.Default.Error,
        color = SentimentNegative,
        containerColor = SentimentNegativeBg,
        tag = "negative"
    ),
    NEUTRAL(
        title = "Neutral",
        icon = Icons.Default.RemoveCircle,
        color = SentimentNeutral,
        containerColor = SentimentNeutralBg,
        tag = "neutral"
    );

    companion object {
        fun fromString(value: String): SentimentType {
            return when (value.trim().lowercase()) {
                "positive" -> POSITIVE
                "negative" -> NEGATIVE
                else -> NEUTRAL
            }
        }
    }
}
