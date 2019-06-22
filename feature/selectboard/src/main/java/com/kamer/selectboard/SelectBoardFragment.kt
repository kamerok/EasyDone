package com.kamer.selectboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.trelloapi.TrelloApi
import com.kamer.trelloapi.TrelloApiProvider
import kotlinx.android.synthetic.main.fragment_select_board.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SelectBoardFragment : Fragment() {

    private lateinit var token: String
    private lateinit var userId: String
    private lateinit var listener: (String) -> Unit

    private val adapter by lazy { BoardsAdapter { listener(it) } }

    private val api: TrelloApi by lazy { TrelloApiProvider.api }

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
            val uiBoards = boards.map { BoardUiModel(it.name) }

            withContext(Dispatchers.Main) {
                adapter.setData(uiBoards)
                progressView.hide()
            }
        }
    }

    data class Dependencies(
        val token: String,
        val userId: String,
        val listener: (String) -> Unit
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SelectBoardFragment().apply {
            token = dependencies.token
            userId = dependencies.userId
            listener = dependencies.listener
        }
    }

}
