package easydone.feature.selectboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.selectboard.R
import easydone.library.trelloapi.TrelloApi
import kotlinx.android.synthetic.main.fragment_select_board.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SelectBoardFragment : Fragment() {

    private lateinit var token: String
    private lateinit var userId: String
    private lateinit var listener: (String) -> Unit
    private lateinit var api: TrelloApi

    private val adapter by lazy { BoardsAdapter { listener(it) } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        loadBoards()
    }

    private fun loadBoards() {
        GlobalScope.launch(Dispatchers.IO) {

            val boards = api.boards(userId, TrelloApi.API_KEY, token)
            val uiBoards = boards.map { BoardUiModel(it.id, it.name) }

            withContext(Dispatchers.Main) {
                adapter.setData(uiBoards)
                progressView.hide()
            }
        }
    }

    data class Dependencies(
        val token: String,
        val userId: String,
        val listener: (String) -> Unit,
        val api: TrelloApi
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SelectBoardFragment().apply {
            token = dependencies.token
            userId = dependencies.userId
            listener = dependencies.listener
            api = dependencies.api
        }
    }

}
