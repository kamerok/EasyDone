package easydone.coreui.design

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


private val primary = Color(0xFF5073F0)
private val urgent = Color(0xFFF3BE00)
private val important = Color(0xFFE14B4B)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) {
            darkColors(
                background = Color(0xFF2D3134),
                primary = primary,
                onPrimary = Color.White,
                onError = Color.White,
                surface = Color(0xFF434343)
            )
        } else {
            lightColors(
                background = Color(0xFFF3F3F3),
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


@Composable
fun EasyDoneAppBar(
    navigationIcon: @Composable () -> Unit = { DefaultBackIcon() },
    actions: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit
) {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background,
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
    )
}

@Composable
private fun DefaultBackIcon() {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    IconButton(onClick = { dispatcher?.onBackPressed() }) {
        Icon(Icons.Default.ArrowBack, "")
    }
}
