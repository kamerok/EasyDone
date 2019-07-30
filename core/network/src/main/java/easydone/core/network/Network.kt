package easydone.core.network

import easydone.core.model.Task
import easydone.library.keyvalue.KeyValueStorage
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID


class Network(
    private val api: TrelloApi,
    private val authInfoHolder: AuthInfoHolder,
    private val idMappings: KeyValueStorage
) {

    private val syncMutex = Mutex(false)

    suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        val boardId = authInfoHolder.getBoardId()!!
        val token = authInfoHolder.getToken()!!
        val board = api.boardData(boardId, TrelloApi.API_KEY, token)
        if (authInfoHolder.getInboxListId().isNullOrEmpty()) {
            authInfoHolder.putInboxListId(board.lists.first().id)
        }
        if (authInfoHolder.getTodoListId().isNullOrEmpty()) {
            authInfoHolder.putTodoListId(board.lists[1].id)
        }
        return@withContext board.cards
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

    suspend fun syncTaskDelta(delta: TaskDelta) = withContext(Dispatchers.IO) {
        syncMutex.withLock {
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
    }

    private fun getListId(type: Task.Type): String {
        return when (type) {
            Task.Type.INBOX -> authInfoHolder.getInboxListId()!!
            Task.Type.TO_DO -> authInfoHolder.getTodoListId()!!
        }
    }

    private fun Card.toTask(id: String, type: Task.Type): Task {
        val date = due?.let { dateFormat.parse(it) }
        return Task(
            id = id,
            type = type,
            title = name,
            description = desc,
            dueDate = date,
            isDone = false
        )
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

}