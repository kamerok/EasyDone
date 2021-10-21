package easydone.core.domain

import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.flow.Flow


interface LocalDataSource {

    suspend fun getChanges(): List<TaskDelta>

    fun observeChangesCount(): Flow<Long>

    fun getTasks(type: Task.Type): List<Task>

    fun getTasksWithDate(): List<Task>

    fun observeTasks(type: Task.Type): Flow<List<Task>>

    suspend fun getTask(id: String): Task

    suspend fun createTask(taskTemplate: TaskTemplate)

    fun updateTask(task: Task)

    fun putData(tasks: List<Task>)

    suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>)

    suspend fun deleteChange(id: Long)

    fun clear()

    suspend fun transaction(body: LocalDataSource.() -> Unit)

}
