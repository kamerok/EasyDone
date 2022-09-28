package easydone.feature.selectboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import easydone.coreui.design.AppTheme


class SelectBoardFragment : Fragment() {

    private lateinit var boards: List<BoardUiModel>
    private lateinit var listener: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            AppTheme {
                LazyColumn(modifier = Modifier.systemBarsPadding()) {
                    items(boards) { board ->
                        BoardItem(
                            board = board,
                            onClick = { listener(board.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }

    data class Dependencies(
        val boards: List<BoardUiModel>,
        val listener: (String) -> Unit
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SelectBoardFragment().apply {
            boards = dependencies.boards
            listener = dependencies.listener
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
