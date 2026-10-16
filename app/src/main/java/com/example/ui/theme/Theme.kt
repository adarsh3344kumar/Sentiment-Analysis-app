package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4F46E5),         // Modern Indigo
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF3730A3),
    secondary = Color(0xFF7C3AED),       // Restrained Violet
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5F3FF),
    onSecondaryContainer = Color(0xFF5B21B6),
    tertiary = Color(0xFF16A34A),        // Accessible Green
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),      // Crisp light neutral
    onBackground = Color(0xFF0F172A),    // Dark Charcoal text
    surface = Color.White,               // Pure White cards
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),  // Subtle slate surface
    onSurfaceVariant = Color(0xFF475569),// Secondary slate text
    outline = Color(0xFFE2E8F0),         // Crisp light border
    outlineVariant = Color(0xFFCBD5E1)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFFA78BFA),
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = Color(0xFF4ADE80),
    onTertiary = Color(0xFF052E16),
    background = Color(0xFF0B0F19),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF131926),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1F293D),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF2E384D),
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun SentilyzerTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT, // LIGHT MODE ONLY BY DEFAULT as requested
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> false // Do not follow device dark theme automatically; light by default
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
