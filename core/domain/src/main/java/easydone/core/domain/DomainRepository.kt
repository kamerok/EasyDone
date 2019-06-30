package easydone.core.domain

import easydone.core.auth.AuthInfoHolder
import easydone.core.domain.model.Task
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Card
import easydone.library.trelloapi.model.NestedBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class DomainRepository(
    private val authInfoHolder: AuthInfoHolder,
    private val api: TrelloApi
) {

    private val channel: BroadcastChannel<NestedBoard> =
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
        api.editCard(
            task.id,
            TrelloApi.API_KEY,
            authInfoHolder.getToken()!!,
            name = task.title,
            desc = task.description
        )
        loadData()
    }

    suspend fun archiveTask(id: String) {
        api.editCard(id, TrelloApi.API_KEY, authInfoHolder.getToken()!!, closed = true)
        loadData()
    }

    suspend fun moveTask(id: String) {
        val (cards, lists) = channel.asFlow().first()
        val card = cards.find { it.id == id }!!
        val newListId = if (lists.first().id == card.idList) {
            lists[1].id
        } else {
            lists.first().id
        }
        api.editCard(id, TrelloApi.API_KEY, authInfoHolder.getToken()!!, listId = newListId)
        loadData()
    }

    suspend fun createTask(title: String, description: String, skipInbox: Boolean) {
        val (_, lists) = channel.asFlow().first()
        api.postCard(
            listId = if (!skipInbox) lists.first().id else lists[1].id,
            name = title,
            desc = description,
            apiKey = TrelloApi.API_KEY,
            token = authInfoHolder.getToken()!!
        )
        loadData()
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        GlobalScope.launch(Dispatchers.IO) {
            val boardId = authInfoHolder.getBoardId()!!
            val token = authInfoHolder.getToken()!!
            val nestedBoard = api.boardData(boardId, TrelloApi.API_KEY, token)
            channel.send(nestedBoard)
        }
    }

    private fun Card.toTask() = Task(id, name, desc)

}