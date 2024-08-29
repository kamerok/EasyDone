package easydone.coreui.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val primary = Color(0xFF5073F0)
val urgent = Color(0xFFF3BE00)
val important = Color(0xFFE14B4B)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val appColorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme(
            background = Color(0xFF1C1C1C),
            primary = primary,
            onPrimary = Color.White,
            onError = Color.White,
            surface = Color(0xFF1C1C1C),
            surfaceVariant = Color(0xFF252525),
            onSurfaceVariant = Color.White,
        )
    } else {
        lightColorScheme(
            background = Color(0xFFF3F3F3),
            primary = primary,
            surface = Color(0xFFF3F3F3),
            surfaceVariant = Color.White,
        )
    }
    MaterialTheme(
        colorScheme = appColorScheme,
        content = content
    )
}