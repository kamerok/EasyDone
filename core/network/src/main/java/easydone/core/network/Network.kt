package easydone.core.network

import easydone.core.model.Task
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Card


class Network(
    private val api: TrelloApi,
    private val authInfoHolder: AuthInfoHolder
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
            val type = when (card.idList) {
                authInfoHolder.getInboxListId() -> Task.Type.INBOX
                else -> Task.Type.TO_DO
            }
            card.toTask(type)
        }
    }

    suspend fun syncTasks(toUpdate: List<Task>, toCreate: List<Task>) {
        toUpdate.forEach { task ->
            api.editCard(
                task.id,
                TrelloApi.API_KEY,
                authInfoHolder.getToken()!!,
                name = task.title,
                desc = task.description,
                closed = task.isDone,
                listId = getListId(task.type)
            )
        }
        toCreate.forEach { task ->
            api.postCard(
                listId = getListId(task.type),
                name = task.title,
                desc = task.description,
                apiKey = TrelloApi.API_KEY,
                token = authInfoHolder.getToken()!!
            )
        }
    }

    private fun getListId(type: Task.Type): String {
        return when (type) {
            Task.Type.INBOX -> authInfoHolder.getInboxListId()!!
            Task.Type.TO_DO -> authInfoHolder.getTodoListId()!!
        }
    }

    private fun Card.toTask(type: Task.Type) = Task(id, type, name, desc, false)

}