package easydone.core.domain

import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass


class DomainRepository(private val localDataSource: LocalDataSource) {

    fun getTasks(type: KClass<out Task.Type>): Flow<List<Task>> = localDataSource.observeTasks(type)

    suspend fun getTask(id: String): Task = localDataSource.getTask(id)

    fun observeTask(id: String): Flow<Task> = localDataSource.observeTask(id)

    suspend fun saveTask(task: Task) {
        require(task.title.isNotEmpty()) { "title should not be empty" }
        localDataSource.updateTask(task)
    }

    suspend fun archiveTask(id: String) {
        val task = localDataSource.getTask(id)
        localDataSource.updateTask(task.copy(isDone = true))
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
                type = if (skipInbox) Task.Type.ToDo else Task.Type.Inbox,
                title = title,
                description = description,
                isUrgent = isUrgent,
                isImportant = isImportant
            )
        )
    }

}
