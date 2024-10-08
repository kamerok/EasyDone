package easydone.service.trello

import easydone.core.domain.RemoteDataSource
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.library.keyvalue.KeyValueStorage
import easydone.service.trello.api.TrelloApi
import easydone.service.trello.api.model.Card
import easydone.service.trello.api.model.NestedBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class TrelloRemoteDataSource(
    private val api: TrelloApi,
    val apiKey: String,
    prefs: KeyValueStorage,
    private val idMappings: KeyValueStorage
) : RemoteDataSource {

    private val authInfoHolder: AuthInfoHolder = AuthInfoHolder(prefs)

    override suspend fun isConnected(): Boolean =
        authInfoHolder.getToken() != null && authInfoHolder.getBoardId() != null

    override suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        val boardId = authInfoHolder.getBoardId()!!
        val token = authInfoHolder.getToken()!!
        val board = api.boardData(boardId, apiKey, token)
        rememberDataAnchors(board)
        board.cards
            .filter {
                it.idList == authInfoHolder.getTodoListId() ||
                        it.idList == authInfoHolder.getInboxListId() ||
                        it.idList == authInfoHolder.getWaitingListId() ||
                        it.idList == authInfoHolder.getProjectsListId() ||
                        it.idList == authInfoHolder.getMaybeListId()
            }
            .map { card -> card.toTask() }
    }

    override suspend fun isTaskKnownOnRemote(id: String): Boolean = idMappings.contains(id)

    override suspend fun updateTask(delta: TaskDelta): Task {
        return withContext(Dispatchers.IO) {
            val serverId: String = requireNotNull(
                idMappings.getString(delta.taskId)
            ) { "Try to update task with $delta but server id was not found" }
            val card = api.editCard(
                serverId,
                apiKey,
                authInfoHolder.getToken()!!,
                name = delta.title,
                desc = delta.description,
                closed = delta.isDone,
                due = delta.getDueDate(),
                listId = delta.type?.let { getListId(it) },
                idLabels = delta.convertMarkersToLabels()
            )
            card.toTask()
        }
    }

    override suspend fun createTask(delta: TaskDelta): Task {
        return withContext(Dispatchers.IO) {
            val card = api.postCard(
                listId = getListId(delta.type!!),
                name = delta.title!!,
                desc = delta.description,
                due = delta.getDueDate(),
                apiKey = apiKey,
                token = authInfoHolder.getToken()!!,
                idLabels = delta.convertMarkersToLabels()
            )
            idMappings.putString(delta.taskId, card.id)
            idMappings.putString(card.id, delta.taskId)
            card.toTask()
        }
    }

    private suspend fun Card.toTask(): Task {
        val localId = idMappings.getString(id, UUID.randomUUID().toString())
        if (!idMappings.contains(id)) {
            idMappings.putString(localId, id)
            idMappings.putString(id, localId)
        }

        val type = when (idList) {
            authInfoHolder.getInboxListId() -> Task.Type.Inbox
            authInfoHolder.getWaitingListId() -> Task.Type.Waiting(
                //todo: handle waiting items with no date
                requireNotNull(due)
                    .let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
            )

            authInfoHolder.getProjectsListId() -> Task.Type.Project
            authInfoHolder.getMaybeListId() -> Task.Type.Maybe
            else -> Task.Type.ToDo
        }
        val isUrgent = idLabels.contains(authInfoHolder.getUrgentLabelId()!!)
        val isImportant = idLabels.contains(authInfoHolder.getImportantLabelId()!!)
        return Task(
            id = localId,
            type = type,
            title = name,
            description = desc,
            markers = Markers(
                isUrgent = isUrgent,
                isImportant = isImportant
            ),
            isDone = false
        )
    }

    private suspend fun TaskDelta.convertMarkersToLabels(): String? {
        return markers?.let { markers ->
            mutableListOf<String>().apply {
                if (markers.isUrgent) add(authInfoHolder.getUrgentLabelId()!!)
                if (markers.isImportant) add(authInfoHolder.getImportantLabelId()!!)
            }.joinToString(separator = ",")
        }
    }

    private fun TaskDelta.getDueDate(): String? =
        type?.let { type ->
            if (type is Task.Type.Waiting) {
                type.date.format(DateTimeFormatter.ISO_DATE)
            } else {
                ""
            }
        }

    override suspend fun disconnect() {
        authInfoHolder.clear()
        idMappings.clear()
    }

    suspend fun connect(token: String, boardId: String) = withContext(Dispatchers.IO) {
        authInfoHolder.putToken(token)
        authInfoHolder.putBoardId(boardId)
    }

    private suspend fun rememberDataAnchors(board: NestedBoard) {
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
        if (authInfoHolder.getProjectsListId().isNullOrEmpty()) {
            authInfoHolder.putProjectsListId(board.lists[4].id)
        }
        if (authInfoHolder.getUrgentLabelId().isNullOrEmpty()) {
            authInfoHolder.putUrgentLabelId(board.labels.find { it.name == "Urgent" }!!.id)
        }
        if (authInfoHolder.getImportantLabelId().isNullOrEmpty()) {
            authInfoHolder.putImportantLabelId(board.labels.find { it.name == "Important" }!!.id)
        }
    }

    private suspend fun getListId(type: Task.Type): String {
        return when (type) {
            is Task.Type.Inbox -> authInfoHolder.getInboxListId()!!
            is Task.Type.ToDo -> authInfoHolder.getTodoListId()!!
            is Task.Type.Waiting -> authInfoHolder.getWaitingListId()!!
            is Task.Type.Project -> authInfoHolder.getProjectsListId()!!
            is Task.Type.Maybe -> authInfoHolder.getMaybeListId()!!
        }
    }

}
