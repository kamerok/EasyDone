package easydone.core.database

import easydone.core.model.Task
import easydone.core.model.TaskTemplate
import kotlinx.coroutines.flow.Flow


interface MyDatabase {

    suspend fun getChanges(): List<ChangeEntry>

    fun observeChangesCount(): Flow<Long>

    suspend fun deleteChange(id: Long)

    fun getTasksStream(type: Task.Type): Flow<List<Task>>

    fun getTasks(type: Task.Type): List<Task>

    suspend fun getTask(id: String): Task

    suspend fun createTask(taskTemplate: TaskTemplate)

    fun updateTask(task: Task)

    fun putData(tasks: List<Task>)

    fun clear()

    fun getTasksWithDate(): List<Task>

    suspend fun transaction(body: MyDatabase.() -> Unit)

}
