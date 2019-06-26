package easydone.feature.createtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.trelloapi.TrelloApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreateTaskFragment : Fragment() {

    private lateinit var token: String
    private lateinit var boardId: String
    private lateinit var api: TrelloApi

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_create_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    data class Dependencies(
        var token: String,
        var boardId: String,
        val api: TrelloApi
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = CreateTaskFragment().apply {
            token = dependencies.token
            boardId = dependencies.boardId
            api = dependencies.api
        }
    }

}