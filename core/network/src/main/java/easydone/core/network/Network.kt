package easydone.core.network

import easydone.core.model.EntityField
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
        return board.cards.map { card ->
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

    suspend fun syncChange(id: String, fields: Map<EntityField, Any>) {
        if (idMappings.contains(id)) {
            val serverId: String = idMappings.getString(id)!!
            api.editCard(
                serverId,
                TrelloApi.API_KEY,
                authInfoHolder.getToken()!!,
                name = fields[EntityField.TITLE] as? String,
                desc = fields[EntityField.DESCRIPTION] as? String,
                closed = fields[EntityField.IS_DONE] as? Boolean,
                listId = (fields[EntityField.TYPE] as? Task.Type)?.let { getListId(it) }
            )
        } else {
            val card = api.postCard(
                listId = getListId(fields[EntityField.TYPE] as Task.Type),
                name = fields[EntityField.TITLE] as String,
                desc = fields[EntityField.DESCRIPTION] as? String,
                apiKey = TrelloApi.API_KEY,
                token = authInfoHolder.getToken()!!
            )
            idMappings.putString(id, card.id)
            idMappings.putString(card.id, id)
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