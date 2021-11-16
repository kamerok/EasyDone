package easydone.coreui.design

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


private val primary = Color(0xFF5073F0)
private val urgent = Color(0xFFF3BE00)
private val important = Color(0xFFE14B4B)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) {
            darkColors(
                background = Color(0xFF1C1C1C),
                primary = primary,
                onPrimary = Color.White,
                onError = Color.White,
                surface = Color(0xFF252525)
            )
        } else {
            lightColors(
                background = Color(0xFFF3F3F3),
                primary = primary
            )
        },
        content = {
            CompositionLocalProvider(LocalElevationOverlay provides null) {
                content()
            }
        }
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
fun IconText(modifier: Modifier = Modifier) {
    Icon(Icons.Default.Subject, "", modifier = modifier)
}


@Composable
fun EasyDoneAppBar(
    navigationIcon: @Composable (() -> Unit)? = { DefaultBackIcon() },
    actions: @Composable RowScope.() -> Unit = {},
    menu: @Composable (ColumnScope.() -> Unit)? = null,
    title: @Composable () -> Unit
) {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background,
        title = title,
        navigationIcon = navigationIcon,
        actions = {
            actions()
            if (menu != null) {
                var showMenu by remember { mutableStateOf(false) }
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, "")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    menu()
                }
            }
        },
    )
}

@Composable
private fun DefaultBackIcon() {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    IconButton(onClick = { dispatcher?.onBackPressed() }) {
        Icon(Icons.Default.ArrowBack, "")
    }
}

data class UiTask(
    val id: String,
    val title: String,
    val hasDescription: Boolean,
    val isUrgent: Boolean,
    val isImportant: Boolean
)

@Composable
fun TaskCard(
    task: UiTask,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = task.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (task.hasDescription || task.isUrgent || task.isImportant) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (task.hasDescription) {
                        IconText()
                    }
                    if (task.isImportant) {
                        IconImportant()
                    }
                    if (task.isUrgent) {
                        IconUrgent()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TaskCardPreview() {
    TaskCard(
        task = UiTask(
            id = "id",
            title = "Title",
            hasDescription = true,
            isUrgent = true,
            isImportant = true
        )
    )
}

@Preview
@Composable
private fun ShortTaskCardPreview() {
    TaskCard(
        task = UiTask(
            id = "id",
            title = "Title",
            hasDescription = false,
            isUrgent = false,
            isImportant = false
        )
    )
}

@Preview(widthDp = 300)
@Composable
private fun LongTaskCardPreview() {
    TaskCard(
        task = UiTask(
            id = "id",
            title = "Title title title title title title title title title title" +
                    " title title title title title title title title title title" +
                    " title title title title title",
            hasDescription = true,
            isUrgent = true,
            isImportant = true
        )
    )
}
