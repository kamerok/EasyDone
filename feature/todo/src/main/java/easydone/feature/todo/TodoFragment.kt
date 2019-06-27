package easydone.feature.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.trelloapi.TrelloApi
import kotlinx.android.synthetic.main.fragment_todo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TodoFragment : Fragment() {

    private lateinit var token: String
    private lateinit var boardId: String
    private lateinit var api: TrelloApi
    private lateinit var navigator: TodoNavigator

    private val adapter by lazy { TodoAdapter { navigator.navigateToTask(it) } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_todo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {
            val lists = api.lists(boardId, TrelloApi.API_KEY, token)
            val cards = api.cards(boardId, TrelloApi.API_KEY, token).filter { it.idList == lists[1].id }
            val tasks = cards.map { TodoTaskUiModel(it.id, it.name) }
            withContext(Dispatchers.Main) {
                adapter.setData(tasks)
            }
        }
    }

    data class Dependencies(
        var token: String,
        var boardId: String,
        val api: TrelloApi,
        val navigator: TodoNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = TodoFragment().apply {
            token = dependencies.token
            boardId = dependencies.boardId
            api = dependencies.api
            navigator = dependencies.navigator
        }
    }

}