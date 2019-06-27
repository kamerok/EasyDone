package easydone.feature.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import easydone.library.trelloapi.TrelloApi
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditTaskFragment : Fragment() {

    private lateinit var id: String
    private lateinit var boardId: String
    private lateinit var token: String
    private lateinit var api: TrelloApi

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_edit_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        GlobalScope.launch(Dispatchers.IO) {
            val card = api.card(id, TrelloApi.API_KEY, token)
            withContext(Dispatchers.Main) {
                titleView.setText(card.name)
            }
        }
        saveView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                api.editCard(id, TrelloApi.API_KEY, token, name = titleView.text.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
        archiveView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                api.editCard(id, TrelloApi.API_KEY, token, closed = true)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Closed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        moveView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val card = api.card(id, TrelloApi.API_KEY, token)
                val lists = api.lists(boardId, TrelloApi.API_KEY, token)
                val newListId = if (lists.first().id == card.idList) {
                    lists[1].id
                } else {
                    lists.first().id
                }
                api.editCard(id, TrelloApi.API_KEY, token, listId = newListId)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Moved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class Dependencies(
        var id: String,
        var boardId: String,
        var token: String,
        val api: TrelloApi
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = EditTaskFragment().apply {
            id = dependencies.id
            boardId = dependencies.boardId
            token = dependencies.token
            api = dependencies.api
        }
    }

}