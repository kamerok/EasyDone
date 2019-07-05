package easydone.core.database

import easydone.core.model.Task
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


class DatabaseImpl : Database {

    private val channel: BroadcastChannel<List<Task>> = ConflatedBroadcastChannel()

    private val tasksToUpdate = mutableListOf<Task>()
    private val tasksToCreate = mutableListOf<Task>()

    override fun getTasks(type: Task.Type): Flow<List<Task>> = flow {
        channel.consumeEach { tasks -> emit(tasks.filter { it.type == type && !it.isDone }) }
    }

    override suspend fun getTasksToUpdate(): List<Task> = tasksToUpdate

    override suspend fun getTasksToCreate(): List<Task> = tasksToCreate

    override suspend fun getTask(id: String): Task {
        val tasks = channel.asFlow().first()
        return tasks.find { it.id == id }!!
    }

    override suspend fun createTask(task: Task) {
        tasksToCreate.add(task)
        val tasks = channel.asFlow().first()
        channel.send(tasks.plus(task))
    }

    override suspend fun updateTask(task: Task) {
        tasksToUpdate.add(task)
        val tasks = channel.asFlow().first()
        channel.send(tasks.map { if (task.id == it.id) task else it })
    }

    override suspend fun putData(tasks: List<Task>) {
        tasksToUpdate.clear()
        tasksToCreate.clear()
        channel.send(tasks)
    }

    override suspend fun clear() = channel.send(emptyList())
}