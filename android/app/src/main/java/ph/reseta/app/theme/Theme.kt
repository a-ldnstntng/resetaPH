package ph.reseta.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ResetaPrimary,
    onPrimary = ResetaOnPrimary,
    primaryContainer = ResetaPrimarySoft,
    onPrimaryContainer = ResetaPrimaryDark,
    secondary = ResetaPrimaryDark,
    onSecondary = Color.White,
    secondaryContainer = ResetaPrimarySoft,
    onSecondaryContainer = ResetaInk,
    tertiary = ResetaAccentOrange,
    onTertiary = Color.White,
    background = ResetaBackground,
    onBackground = ResetaInk,
    surface = ResetaSurface,
    onSurface = ResetaInk,
    surfaceVariant = ResetaPrimarySoft,
    onSurfaceVariant = ResetaInkMuted,
    outline = ResetaBorder,
    error = ResetaDanger,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF26A69A),
    onPrimary = Color(0xFF003827),
    primaryContainer = Color(0xFF00513B),
    onPrimaryContainer = Color(0xFFA3F2D4),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003730),
    background = Color(0xFF101915),
    onBackground = Color(0xFFE0E6E2),
    surface = Color(0xFF18231E),
    onSurface = Color(0xFFE0E6E2),
    outline = Color(0xFF2D3C35),
    error = Color(0xFFEF9A9A),
    onError = Color(0xFF5C0000)
)

@Composable
fun ResetaPHTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
