package easydone.coreui.design

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TaskCard(
    task: UiTask,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(CardDefaults.cardColors().containerColor)
                    .shareTaskBounds(task.id)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = task.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.shareTaskTitle(task.id)
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
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun TaskCardPreview() {
    AppTheme {
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
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun ShortTaskCardPreview() {
    AppTheme {
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
}

@Preview(widthDp = 300)
@Preview(
    widthDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun LongTaskCardPreview() {
    AppTheme {
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
}