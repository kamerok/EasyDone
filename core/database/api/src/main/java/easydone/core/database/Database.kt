package easydone.core.database

import easydone.core.model.Task
import kotlinx.coroutines.flow.Flow


interface Database {

    fun getTasks(type: Task.Type): Flow<List<Task>>

    suspend fun getChanges(): List<Pair<Action, Task>>

    suspend fun getTask(id: String): Task

    suspend fun createTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun putData(tasks: List<Task>)

    suspend fun clear()

}