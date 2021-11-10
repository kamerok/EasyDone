package easydone.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.kamer.home.R
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.IconImportant
import easydone.coreui.design.IconText
import easydone.coreui.design.IconUrgent


@Composable
internal fun HomeScreen() {
    AppTheme {
        ProvideWindowInsets {
            FullscreenContent {
                Column {
                    EasyDoneAppBar(navigationIcon = null) {
                        Text(stringResource(R.string.app_name))
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        InboxMessage(
                            count = 10,
                            onSort = {}
                        )
                        Title("ToDo")
                    }
                }
            }
        }
    }
}

@Composable
private fun FullscreenContent(
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            //to draw under paddings
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        content()
    }
}

@Composable
private fun InboxMessage(
    count: Int,
    onSort: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp)
    ) {
        //TODO: extract res
        Text(
            text = "Inbox is not empty: $count",
            style = MaterialTheme.typography.h5
        )
        Text(
            text = "SORT",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.clickable(onClick = onSort)
        )
    }
}

@Composable
private fun Title(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h5
    )
}

data class UiTask(
    val title: String,
    val hasDescription: Boolean,
    val isUrgent: Boolean,
    val isImportant: Boolean
)

@Composable
private fun TaskCard(task: UiTask) {
    Card {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = task.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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

@Composable
private fun MoreButton(count: Int) {
    //TODO
}

@Preview(showBackground = true)
@Composable
private fun ScreenPreview() {
    HomeScreen()
}

@Preview
@Composable
private fun TaskCardPreview() {
    TaskCard(
        task = UiTask(
            title = "Title",
            hasDescription = true,
            isUrgent = true,
            isImportant = true
        )
    )
}

@Preview(widthDp = 300)
@Composable
private fun LongTaskCardPreview() {
    TaskCard(
        task = UiTask(
            title = "Title title title title title title title title title title" +
                    " title title title title title title title title title title" +
                    " title title title title title",
            hasDescription = true,
            isUrgent = true,
            isImportant = true
        )
    )
}
