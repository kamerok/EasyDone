package easydone.core.domain

import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass


interface LocalDataSource {

    suspend fun getChanges(): List<TaskDelta>

    fun observeChangesCount(): Flow<Long>

    fun observeTasks(type: KClass<out Task.Type>): Flow<List<Task>>

    fun observeTask(id: String): Flow<Task>

    suspend fun getTask(id: String): Task

    suspend fun createTask(taskTemplate: TaskTemplate)

    suspend fun updateTask(task: Task)

    /**
     * Second parameter required to write change deltas
     */
    suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>)

    suspend fun deleteChange(id: Long)

}
