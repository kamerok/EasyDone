package easydone.core.database

import easydone.core.model.Task
import easydone.core.model.TaskTemplate
import kotlinx.coroutines.flow.Flow


interface MyDatabase {

    fun getTasks(type: Task.Type): Flow<List<Task>>

    suspend fun getChanges(): List<Pair<Action, Task>>

    suspend fun getTask(id: String): Task

    suspend fun createTask(taskTemplate: TaskTemplate)

    suspend fun updateTask(task: Task)

    suspend fun putData(tasks: List<Task>)

    suspend fun clear()

}