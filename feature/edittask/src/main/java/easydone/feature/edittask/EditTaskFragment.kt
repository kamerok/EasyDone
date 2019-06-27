package easydone.feature.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.trelloapi.TrelloApi
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditTaskFragment : Fragment() {

    private lateinit var id: String
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
    }

    data class Dependencies(
        var id: String,
        var token: String,
        val api: TrelloApi
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = EditTaskFragment().apply {
            id = dependencies.id
            token = dependencies.token
            api = dependencies.api
        }
    }

}