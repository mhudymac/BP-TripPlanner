package kmp.android.shared.style

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

// https://coolors.co/f5ab00-b8a422-9aa133-7b9d44-d95700-e0e0e0-f0f0f0
val LightColors = lightColorScheme(
    primary = Color(0xFFF5AB00),
    //primaryVariant = Color(0xFFB8A422),

    secondary = Color(0xFF7B9D44),
    //secondaryVariant = Color(0xFF9AA133),

    background = Color(0xFFF0F0F0),
    surface = Color(0xFFE0E0E0),

    error = Color(0xFFD95700),

    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
    onError = Color(0xFF000000),

)

// https://coolors.co/f5ab00-b8a422-9aa133-7b9d44-d95700-1f1f1f-141414
val DarkColors = darkColorScheme(
    primary = Color(0xFFF5AB00),
    //primaryVariant = Color(0xFFB8A422),

    secondary = Color(0xFF7B9D44),
    //secondaryVariant = Color(0xFF9AA133),

    background = Color(0xFF141414),
    surface = Color(0xFF1F1F1F),

    error = Color(0xFFD95700),

    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onError = Color(0xFFFFFFFF),

)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colors = when {
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }


    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
