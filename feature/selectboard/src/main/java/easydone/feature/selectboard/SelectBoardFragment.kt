package easydone.feature.selectboard


import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.kamer.selectboard.R


class SelectBoardFragment : Fragment(R.layout.fragment_select_board) {

    private lateinit var boards: List<BoardUiModel>
    private lateinit var listener: (String) -> Unit

    private val adapter by lazy { BoardsAdapter { listener(it) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
        adapter.setData(boards)

        view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            insets.consumeSystemWindowInsets()
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
