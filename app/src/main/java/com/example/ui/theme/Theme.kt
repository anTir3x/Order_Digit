package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = Color.White,
    primaryContainer = IndigoDark,
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = OrderCyanLight,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = OrderCyanContainer,
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = DigitAmberLight,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = DigitAmberContainer,
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = BackgroundDark,
    onBackground = Color(0xFFF9FAFB),
    surface = SurfaceDark,
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = CardBorderDark,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = IndigoDark,
    secondary = OrderCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF0E7490),
    tertiary = DigitAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFFB45309),
    background = BackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = SurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = CardBorderLight,
    error = ErrorRed
)

@Composable
fun OrderDigitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinctive cyber-deduction styling by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
