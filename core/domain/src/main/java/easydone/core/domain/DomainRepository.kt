package easydone.core.domain

import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.flow.Flow


class DomainRepository(private val localDataSource: LocalDataSource) {

    fun getTasks(type: Task.Type): Flow<List<Task>> = localDataSource.observeTasks(type)

    suspend fun getTask(id: String): Task = localDataSource.getTask(id)

    suspend fun saveTask(task: Task) {
        require(task.title.isNotEmpty()) { "title should not be empty" }
        localDataSource.transaction { updateTask(task) }
    }

    suspend fun archiveTask(id: String) {
        val task = localDataSource.getTask(id)
        localDataSource.transaction { updateTask(task.copy(isDone = true)) }
    }

    suspend fun moveTask(id: String) {
        val task = localDataSource.getTask(id)
        localDataSource.transaction {
            updateTask(
                task.copy(
                    type = if (task.type == Task.Type.INBOX) Task.Type.TO_DO else Task.Type.INBOX
                )
            )
        }
    }

    suspend fun switchUrgent(taskId: String) {
        val task = localDataSource.getTask(taskId)
        localDataSource.transaction {
            updateTask(
                task.copy(markers = task.markers.copy(isUrgent = !task.markers.isUrgent))
            )
        }
    }

    suspend fun switchImportant(taskId: String) {
        val task = localDataSource.getTask(taskId)
        localDataSource.transaction {
            updateTask(
                task.copy(markers = task.markers.copy(isImportant = !task.markers.isImportant))
            )
        }
    }

    suspend fun createTask(
        title: String,
        description: String,
        skipInbox: Boolean,
        isUrgent: Boolean,
        isImportant: Boolean
    ) {
        if (title.isEmpty()) throw IllegalArgumentException("title should not be empty")
        localDataSource.createTask(
            TaskTemplate(
                type = if (skipInbox) Task.Type.TO_DO else Task.Type.INBOX,
                title = title,
                description = description,
                isUrgent = isUrgent,
                isImportant = isImportant
            )
        )
    }

}
