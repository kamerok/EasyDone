package easydone.core.domain

import easydone.core.database.MyDatabase
import easydone.core.model.Task
import easydone.core.model.TaskTemplate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow


class DomainRepository(private val database: MyDatabase) {

    @ExperimentalCoroutinesApi
    fun getTasks(type: Task.Type): Flow<List<Task>> = database.getTasksStream(type)

    suspend fun getTask(id: String): Task = database.getTask(id)

    suspend fun saveTask(task: Task) {
        require(task.title.isNotEmpty()) { "title should not be empty" }
        database.transaction { updateTask(task) }
    }

    suspend fun archiveTask(id: String) {
        val task = database.getTask(id)
        database.transaction { updateTask(task.copy(isDone = true)) }
    }

    suspend fun moveTask(id: String) {
        val task = database.getTask(id)
        database.transaction {
            updateTask(
                task.copy(
                    type = if (task.type == Task.Type.INBOX) Task.Type.TO_DO else Task.Type.INBOX
                )
            )
        }
    }

    suspend fun createTask(title: String, description: String, skipInbox: Boolean) {
        if (title.isEmpty()) throw IllegalArgumentException("title should not be empty")
        database.createTask(
            TaskTemplate(
                type = if (skipInbox) Task.Type.TO_DO else Task.Type.INBOX,
                title = title,
                description = description
            )
        )
    }

}
