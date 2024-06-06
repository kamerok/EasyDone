package easydone.feature.selectboard

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import easydone.coreui.design.AppTheme


@Composable
fun SelectBoardScreen(
    boards: List<BoardUiModel>,
    onBoardSelected: (String) -> Unit
) {
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            LazyColumn(modifier = Modifier.systemBarsPadding()) {
                items(boards) { board ->
                    BoardItem(
                        board = board,
                        onClick = { onBoardSelected(board.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

@Composable
private fun BoardItem(board: BoardUiModel, onClick: () -> Unit) {
    Text(
        text = board.name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    )
}

@Preview(widthDp = 100, heightDp = 100, showBackground = true)
@Composable
private fun BoardPreview() {
    AppTheme {
        Column {
            BoardItem(board = BoardUiModel("id", "name")) {

            }
            BoardItem(board = BoardUiModel("id", "name")) {

            }
        }
    }
}

@Preview(
    widthDp = 100, heightDp = 100, showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun BoardPreviewDark() {
    AppTheme {
        Column {
            BoardItem(board = BoardUiModel("id", "name")) {

            }
            BoardItem(board = BoardUiModel("id", "name")) {

            }
        }
    }
}