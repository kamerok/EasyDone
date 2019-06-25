package com.kamer.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kamer.trelloapi.TrelloApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class InboxFragment : Fragment() {

    private lateinit var token: String
    private lateinit var boardId: String
    private lateinit var api: TrelloApi

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_inbox, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        GlobalScope.launch {
            api.cards(boardId, TrelloApi.API_KEY, token)
        }
    }

    data class Dependencies(
        var token: String,
        var boardId: String,
        val api: TrelloApi
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = InboxFragment().apply {
            token = dependencies.token
            boardId = dependencies.boardId
            api = dependencies.api
        }
    }

}