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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


class DomainRepository(
    private val authInfoHolder: AuthInfoHolder,
    private val api: TrelloApi
) {

    private val channel: BroadcastChannel<Pair<List<Card>, List<CardList>>> =
        ConflatedBroadcastChannel()

    fun getTasks(isInbox: Boolean): Flow<List<Task>> = flow {
        channel.consumeEach { (cards, lists) ->
            val listId = lists[if (isInbox) 0 else 1].id
            val filteredCards = cards.filter { it.idList == listId }
            emit(filteredCards.map { it.toTask() })
        }
    }

    suspend fun getTask(id: String): Task {
        val (cards, _) = channel.asFlow().first()
        return cards.find { it.id == id }!!.toTask()
    }

    suspend fun saveTask(task: Task) {
        api.editCard(task.id, TrelloApi.API_KEY, authInfoHolder.getToken()!!, name = task.title)
        loadData()
    }

    suspend fun archiveTask(id: String) {
        api.editCard(id, TrelloApi.API_KEY, authInfoHolder.getToken()!!, closed = true)
        loadData()
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        GlobalScope.launch(Dispatchers.IO) {
            val boardId = authInfoHolder.getBoardId()!!
            val token = authInfoHolder.getToken()!!
            val lists = async { api.lists(boardId, TrelloApi.API_KEY, token) }
            val cards = async { api.cards(boardId, TrelloApi.API_KEY, token) }
            channel.send(cards.await() to lists.await())
        }
    }

    private fun Card.toTask() = Task(id, name)

}