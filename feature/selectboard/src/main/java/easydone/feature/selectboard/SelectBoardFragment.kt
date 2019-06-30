package easydone.feature.selectboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.selectboard.R
import kotlinx.android.synthetic.main.fragment_select_board.*


class SelectBoardFragment : Fragment() {

    private lateinit var boards: List<BoardUiModel>
    private lateinit var listener: (String) -> Unit

    private val adapter by lazy { BoardsAdapter { listener(it) } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_select_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        adapter.setData(boards)
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
