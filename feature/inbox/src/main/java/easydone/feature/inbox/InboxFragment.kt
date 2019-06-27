package easydone.feature.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.inbox.R
import easydone.library.trelloapi.TrelloApi
import kotlinx.android.synthetic.main.fragment_inbox.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class InboxFragment : Fragment() {

    private lateinit var token: String
    private lateinit var boardId: String
    private lateinit var api: TrelloApi
    private lateinit var navigator: InboxNavigator

    private val adapter by lazy { InboxAdapter { navigator.navigateToTask(it) } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_inbox, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {
            val lists = api.lists(boardId, TrelloApi.API_KEY, token)
            val cards = api.cards(boardId, TrelloApi.API_KEY, token).filter { it.idList == lists.first().id }
            val tasks = cards.map { InboxTaskUiModel(it.id, it.name) }
            withContext(Dispatchers.Main) {
                adapter.setData(tasks)
            }
        }
    }

    data class Dependencies(
        var token: String,
        var boardId: String,
        val api: TrelloApi,
        val navigator: InboxNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = InboxFragment().apply {
            token = dependencies.token
            boardId = dependencies.boardId
            api = dependencies.api
            navigator = dependencies.navigator
        }
    }

}