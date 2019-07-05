package easydone.core.domain

import easydone.core.auth.AuthInfoHolder
import easydone.core.database.Database
import easydone.core.model.Task
import easydone.library.trelloapi.TrelloApi
import easydone.library.trelloapi.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class DomainRepository(
    private val authInfoHolder: AuthInfoHolder,
    private val database: Database,
    private val api: TrelloApi
) {

    //TODO: use type
    @ExperimentalCoroutinesApi
    fun getTasks(isInbox: Boolean): Flow<List<Task>> =
        database.getTasks(if (isInbox) Task.Type.INBOX else Task.Type.TODO)

    suspend fun getTask(id: String): Task = database.getTask(id)

    suspend fun saveTask(task: Task) = database.updateTask(task)

    suspend fun archiveTask(id: String) {
        val task = database.getTask(id)
        database.updateTask(task.copy(isDone = true))
    }

    suspend fun moveTask(id: String) {
        val task = database.getTask(id)
        database.updateTask(task.copy(type = if (task.type == Task.Type.INBOX) Task.Type.TODO else Task.Type.INBOX))
    }

    suspend fun createTask(title: String, description: String, skipInbox: Boolean) {
        database.createTask(
            Task(
                "",
                if (skipInbox) Task.Type.TODO else Task.Type.INBOX,
                title,
                description,
                false
            )
        )
    }

    fun refresh() {
        GlobalScope.launch(Dispatchers.IO) {
            val boardId = authInfoHolder.getBoardId()!!
            val token = authInfoHolder.getToken()!!
            val oldBoard = api.boardData(boardId, TrelloApi.API_KEY, token)

            val taskToUpdate = database.getTasksToUpdate()
            taskToUpdate.forEach { task ->
                api.editCard(
                    task.id,
                    TrelloApi.API_KEY,
                    authInfoHolder.getToken()!!,
                    name = task.title,
                    desc = task.description,
                    closed = task.isDone,
                    listId = when (task.type) {
                        Task.Type.INBOX -> oldBoard.lists.first().id
                        Task.Type.TODO -> oldBoard.lists[1].id
                    }
                )
            }
            val taskToCreate = database.getTasksToCreate()
            taskToCreate.forEach { task ->
                api.postCard(
                    listId = when (task.type) {
                        Task.Type.INBOX -> oldBoard.lists.first().id
                        Task.Type.TODO -> oldBoard.lists[1].id
                    },
                    name = task.title,
                    desc = task.description,
                    apiKey = TrelloApi.API_KEY,
                    token = authInfoHolder.getToken()!!
                )
            }

            val nestedBoard = api.boardData(boardId, TrelloApi.API_KEY, token)
            database.putData(nestedBoard.cards.map { card ->
                card.toTask(if (nestedBoard.lists.first().id == card.idList) Task.Type.INBOX else Task.Type.TODO)
            })
        }
    }

    private fun Card.toTask(type: Task.Type) = Task(id, type, name, desc, false)

}