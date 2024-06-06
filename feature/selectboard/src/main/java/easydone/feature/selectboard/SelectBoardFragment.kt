package easydone.feature.selectboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment


class SelectBoardFragment : Fragment() {

    private lateinit var boards: List<BoardUiModel>
    private lateinit var listener: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            SelectBoardScreen(boards = boards, onBoardSelected = listener)
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
