package easydone.core.database

import easydone.core.model.Task
import kotlinx.coroutines.flow.Flow


interface Database {

    fun getTasks(type: Task.Type): Flow<List<Task>>

    //TODO: refactor to action log
    suspend fun getTasksToUpdate(): List<Task>

    suspend fun getTasksToCreate(): List<Task>

    suspend fun getTask(id: String): Task

    suspend fun createTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun putData(tasks: List<Task>)

    suspend fun clear()

}