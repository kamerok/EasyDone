package easydone.core.network

import easydone.core.domain.Network
import easydone.core.domain.TaskDelta
import easydone.core.model.Markers
import easydone.core.model.Task
import easydone.library.keyvalue.KeyValueStorage
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Card
import easydone.library.trelloapi.model.NestedBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.UUID

class NetworkImpl(
    private val api: TrelloApi,
    private val apiKey: String,
    private val authInfoHolder: AuthInfoHolder,
    private val idMappings: KeyValueStorage
) : Network {

    private val syncMutex = Mutex(false)

    override suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        val boardId = authInfoHolder.getBoardId()!!
        val token = authInfoHolder.getToken()!!
        val board = api.boardData(boardId, apiKey, token)
        rememberDataAnchors(board)
        return@withContext board.cards
            .filter {
                it.idList == authInfoHolder.getTodoListId() ||
                        it.idList == authInfoHolder.getInboxListId() ||
                        it.idList == authInfoHolder.getWaitingListId() ||
                        it.idList == authInfoHolder.getMaybeListId()
            }
            .map { card ->
                val localId = idMappings.getString(card.id, UUID.randomUUID().toString())
                if (!idMappings.contains(card.id)) {
                    idMappings.putString(localId, card.id)
                    idMappings.putString(card.id, localId)
                }

                val type = when (card.idList) {
                    authInfoHolder.getInboxListId() -> Task.Type.INBOX
                    authInfoHolder.getWaitingListId() -> Task.Type.WAITING
                    authInfoHolder.getMaybeListId() -> Task.Type.MAYBE
                    else -> Task.Type.TO_DO
                }
                val isUrgent = card.idLabels.contains(authInfoHolder.getUrgentLabelId()!!)
                val isImportant = card.idLabels.contains(authInfoHolder.getImportantLabelId()!!)
                card.toTask(localId, type, isUrgent, isImportant)
            }
    }

    override suspend fun syncTaskDelta(delta: TaskDelta) {
        withContext(Dispatchers.IO) {
            syncMutex.withLock {
                val labels = delta.markers?.let { markers ->
                    mutableListOf<String>().apply {
                        if (markers.isUrgent) add(authInfoHolder.getUrgentLabelId()!!)
                        if (markers.isImportant) add(authInfoHolder.getImportantLabelId()!!)
                    }.joinToString(separator = ",")
                }
                if (idMappings.contains(delta.id)) {
                    val serverId: String = idMappings.getString(delta.id)!!
                    api.editCard(
                        serverId,
                        apiKey,
                        authInfoHolder.getToken()!!,
                        name = delta.title,
                        desc = delta.description,
                        closed = delta.isDone,
                        due = if (delta.dueDateChanged) {
                            delta.dueDate?.format(DateTimeFormatter.ISO_DATE) ?: ""
                        } else {
                            null
                        },
                        listId = delta.type?.let { getListId(it) },
                        idLabels = labels
                    )
                } else {
                    val card = api.postCard(
                        listId = getListId(delta.type!!),
                        name = delta.title!!,
                        desc = delta.description,
                        apiKey = apiKey,
                        token = authInfoHolder.getToken()!!,
                        idLabels = labels
                    )
                    idMappings.putString(delta.id, card.id)
                    idMappings.putString(card.id, delta.id)
                }
            }
        }
    }

    private fun rememberDataAnchors(board: NestedBoard) {
        if (authInfoHolder.getInboxListId().isNullOrEmpty()) {
            authInfoHolder.putInboxListId(board.lists.first().id)
        }
        if (authInfoHolder.getTodoListId().isNullOrEmpty()) {
            authInfoHolder.putTodoListId(board.lists[1].id)
        }
        if (authInfoHolder.getWaitingListId().isNullOrEmpty()) {
            authInfoHolder.putWaitingListId(board.lists[2].id)
        }
        if (authInfoHolder.getMaybeListId().isNullOrEmpty()) {
            authInfoHolder.putMaybeListId(board.lists[3].id)
        }
        if (authInfoHolder.getUrgentLabelId().isNullOrEmpty()) {
            authInfoHolder.putUrgentLabelId(board.labels.find { it.name == "Urgent" }!!.id)
        }
        if (authInfoHolder.getImportantLabelId().isNullOrEmpty()) {
            authInfoHolder.putImportantLabelId(board.labels.find { it.name == "Important" }!!.id)
        }
    }

    private fun getListId(type: Task.Type): String {
        return when (type) {
            Task.Type.INBOX -> authInfoHolder.getInboxListId()!!
            Task.Type.TO_DO -> authInfoHolder.getTodoListId()!!
            Task.Type.WAITING -> authInfoHolder.getWaitingListId()!!
            Task.Type.MAYBE -> authInfoHolder.getMaybeListId()!!
        }
    }

    private fun Card.toTask(
        id: String,
        type: Task.Type,
        isUrgent: Boolean,
        isImportant: Boolean
    ): Task {
        val date = due?.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
        return Task(
            id = id,
            type = type,
            title = name,
            description = desc,
            dueDate = date,
            markers = Markers(
                isUrgent = isUrgent,
                isImportant = isImportant
            ),
            isDone = false
        )
    }

}
