package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Indigo / Violet Brand Palette
val BrandIndigo = Color(0xFF4F46E5)       // Primary Action & Accent
val BrandIndigoLight = Color(0xFF6366F1)
val BrandIndigoDark = Color(0xFF3730A3)
val BrandIndigoContainer = Color(0xFFEEF2FF)
val OnBrandIndigoContainer = Color(0xFF312E81)

val BrandViolet = Color(0xFF7C3AED)
val BrandVioletLight = Color(0xFF8B5CF6)
val BrandVioletContainer = Color(0xFFF5F3FF)

// Status & Semantic Sentiment Colors
val SentimentPositive = Color(0xFF10B981)          // Emerald 500
val SentimentPositiveDark = Color(0xFF059669)      // Emerald 600
val SentimentPositiveBg = Color(0xFFECFDF5)        // Emerald 50
val SentimentPositiveBorder = Color(0xFFA7F3D0)    // Emerald 200

val SentimentNegative = Color(0xFFF43F5E)          // Rose 500
val SentimentNegativeDark = Color(0xFFE11D48)      // Rose 600
val SentimentNegativeBg = Color(0xFFFFF1F2)        // Rose 50
val SentimentNegativeBorder = Color(0xFFFECDD3)    // Rose 200

val SentimentNeutral = Color(0xFF0EA5E9)           // Sky 500
val SentimentNeutralDark = Color(0xFF0284C7)       // Sky 600
val SentimentNeutralBg = Color(0xFFF0F9FF)         // Sky 50
val SentimentNeutralBorder = Color(0xFFBAE6FD)     // Sky 200

// Neutral / Gray Gradients & Slate Palette
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate600 = Color(0xFF475569)
val Slate700 = Color(0xFF334155)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)
val Slate950 = Color(0xFF0B0F19)

// Backwards-compatible aliases so existing references continue to work seamlessly
val IndigoPrimary = BrandIndigo
val IndigoLight = BrandIndigoLight
val IndigoDark = BrandIndigoDark
val IndigoContainer = BrandIndigoContainer
val OnIndigoContainer = OnBrandIndigoContainer

val CyanAccent = Color(0xFF06B6D4)
val CyanContainer = Color(0xFF083344)

val SentimentPositiveLight = SentimentPositiveBorder
val SentimentNegativeLight = SentimentNegativeBorder
val SentimentNeutralLight = SentimentNeutralBorder

val DarkBackground = Slate950
val DarkSurface = Color(0xFF131926)
val DarkSurfaceVariant = Color(0xFF1F293D)
val DarkBorder = Color(0xFF2E384D)
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Slate600
val TextMuted = Slate400
