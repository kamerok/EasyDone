package easydone.coreui.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


private val primary = Color(0xFF5073F0)
private val urgent = Color(0xFF5073F0)
private val important = Color(0xFFE14B4B)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) {
            darkColors(
                background = Color(0xFF2D3134),
                primary = primary,
                onPrimary = Color.White
            )
        } else {
            lightColors(
                primary = primary
            )
        },
        content = content
    )
}

@Composable
fun IconUrgent(modifier: Modifier = Modifier) {
    Icon(Icons.Default.Bolt, "", tint = urgent, modifier = modifier)
}

@Composable
fun IconImportant(modifier: Modifier = Modifier) {
    Icon(Icons.Default.PriorityHigh, "", tint = important, modifier = modifier)
}
