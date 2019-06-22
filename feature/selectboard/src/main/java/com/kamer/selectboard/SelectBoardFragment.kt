package com.kamer.selectboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_select_board.*


class SelectBoardFragment : Fragment() {

    private lateinit var token: String
    private lateinit var listener: (String) -> Unit

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
        adapter.setData((0..100).map { BoardUiModel("board $it") })
        progressView.hide()
    }

    data class Dependencies(
        val token: String,
        val listener: (String) -> Unit
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SelectBoardFragment().apply {
            token = dependencies.token
            listener = dependencies.listener
        }
    }

}
