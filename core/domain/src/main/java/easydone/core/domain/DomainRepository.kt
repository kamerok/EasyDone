package easydone.core.domain

import easydone.core.database.Database
import easydone.core.model.Task
import easydone.core.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class DomainRepository(
    private val database: Database,
    private val network: Network
) {

    @ExperimentalCoroutinesApi
    fun getTasks(type: Task.Type): Flow<List<Task>> = database.getTasks(type)

    suspend fun getTask(id: String): Task = database.getTask(id)

    suspend fun saveTask(task: Task) = database.updateTask(task)

    suspend fun archiveTask(id: String) {
        val task = database.getTask(id)
        database.updateTask(task.copy(isDone = true))
    }

    suspend fun moveTask(id: String) {
        val task = database.getTask(id)
        database.updateTask(task.copy(type = if (task.type == Task.Type.INBOX) Task.Type.TO_DO else Task.Type.INBOX))
    }

    suspend fun createTask(title: String, description: String, skipInbox: Boolean) {
        database.createTask(
            Task(
                id = "",
                type = if (skipInbox) Task.Type.TO_DO else Task.Type.INBOX,
                title = title,
                description = description,
                isDone = false
            )
        )
    }

    fun refresh() {
        GlobalScope.launch(Dispatchers.IO) {
            network.syncTasks(
                toUpdate = database.getTasksToUpdate(),
                toCreate = database.getTasksToCreate()
            )
            database.putData(network.getAllTasks())
        }
    }

}