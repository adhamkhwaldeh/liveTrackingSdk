package com.kerberos.trackingSdk.ui.theme.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class AppTheme {
    LIGHT, DARK, RED
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val RedColorScheme = lightColorScheme(
    primary = Red500,
    secondary = Red200,
    tertiary = Red700
)

@Composable
fun MyApplicationTheme(
    theme: AppTheme = AppTheme.LIGHT,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            when (theme) {
                AppTheme.LIGHT -> dynamicLightColorScheme(context)
                AppTheme.DARK -> dynamicDarkColorScheme(context)
                AppTheme.RED -> RedColorScheme // Dynamic red is not a thing, so fallback
            }
        }
        else -> {
            when (theme) {
                AppTheme.LIGHT -> LightColorScheme
                AppTheme.DARK -> DarkColorScheme
                AppTheme.RED -> RedColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}