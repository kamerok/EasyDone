package easydone.core.network

import easydone.core.model.Task
import easydone.library.keyvalue.KeyValueStorage
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Card
import java.util.UUID


class Network(
    private val api: TrelloApi,
    private val authInfoHolder: AuthInfoHolder,
    private val idMappings: KeyValueStorage
) {

    suspend fun getAllTasks(): List<Task> {
        val boardId = authInfoHolder.getBoardId()!!
        val token = authInfoHolder.getToken()!!
        val board = api.boardData(boardId, TrelloApi.API_KEY, token)
        if (authInfoHolder.getInboxListId().isNullOrEmpty()) {
            authInfoHolder.putInboxListId(board.lists.first().id)
        }
        if (authInfoHolder.getTodoListId().isNullOrEmpty()) {
            authInfoHolder.putTodoListId(board.lists[1].id)
        }
        return board.cards
            .filter { it.idList == authInfoHolder.getTodoListId() || it.idList == authInfoHolder.getInboxListId() }
            .map { card ->
                val localId = idMappings.getString(card.id, UUID.randomUUID().toString())
                if (!idMappings.contains(card.id)) {
                    idMappings.putString(localId, card.id)
                    idMappings.putString(card.id, localId)
                }

                val type = when (card.idList) {
                    authInfoHolder.getInboxListId() -> Task.Type.INBOX
                    else -> Task.Type.TO_DO
                }
                card.toTask(localId, type)
            }
    }

    suspend fun syncTaskDelta(delta: TaskDelta) {
        if (idMappings.contains(delta.id)) {
            val serverId: String = idMappings.getString(delta.id)!!
            api.editCard(
                serverId,
                TrelloApi.API_KEY,
                authInfoHolder.getToken()!!,
                name = delta.title,
                desc = delta.description,
                closed = delta.isDone,
                listId = delta.type?.let { getListId(it) }
            )
        } else {
            val card = api.postCard(
                listId = getListId(delta.type!!),
                name = delta.title!!,
                desc = delta.description,
                apiKey = TrelloApi.API_KEY,
                token = authInfoHolder.getToken()!!
            )
            idMappings.putString(delta.id, card.id)
            idMappings.putString(card.id, delta.id)
        }
    }

    private fun getListId(type: Task.Type): String {
        return when (type) {
            Task.Type.INBOX -> authInfoHolder.getInboxListId()!!
            Task.Type.TO_DO -> authInfoHolder.getTodoListId()!!
        }
    }

    private fun Card.toTask(id: String, type: Task.Type) = Task(id, type, name, desc, false)

}