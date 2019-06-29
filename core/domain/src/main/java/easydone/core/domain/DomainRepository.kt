package easydone.core.domain

import easydone.core.auth.AuthInfoHolder
import easydone.core.domain.model.Task
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Card
import easydone.library.trelloapi.model.CardList
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class DomainRepository(
    private val authInfoHolder: AuthInfoHolder,
    private val trelloApi: TrelloApi
) {

    private val channel: BroadcastChannel<Pair<List<Card>, List<CardList>>> =
        ConflatedBroadcastChannel()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            val boardId = authInfoHolder.getBoardId()!!
            val token = authInfoHolder.getToken()!!
            val lists = async { trelloApi.lists(boardId, TrelloApi.API_KEY, token) }
            val cards = async { trelloApi.cards(boardId, TrelloApi.API_KEY, token) }
            channel.send(cards.await() to lists.await())
        }
    }

    fun getTasks(isInbox: Boolean): Flow<List<Task>> = flow {
        channel.consumeEach { (cards, lists) ->
            val listId = lists[if (isInbox) 0 else 1].id
            val filteredCards = cards.filter { it.idList == listId }
            emit(filteredCards.map { Task(it.id, it.name) })
        }
    }

}