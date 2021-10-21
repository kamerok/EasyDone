package easydone.core.domain.database

import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.flow.Flow


interface Database {

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

    suspend fun transaction(body: Database.() -> Unit)

}
