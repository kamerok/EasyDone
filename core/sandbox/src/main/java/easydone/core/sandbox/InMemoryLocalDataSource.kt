package easydone.core.sandbox

import easydone.core.domain.LocalDataSource
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.reflect.KClass

class InMemoryLocalDataSource : LocalDataSource {
        private val state: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())

        override suspend fun getChanges(): List<TaskDelta> = emptyList()

        override fun observeChangesCount(): Flow<Long> = flowOf()

        override fun observeTasks(type: KClass<out Task.Type>): Flow<List<Task>> =
            state
                .map { it.filter { task -> !task.isDone && type == task.type::class } }
                .distinctUntilChanged()

        override fun observeTask(id: String): Flow<Task> =
            state
                .map { requireNotNull(it.find { task -> task.id == id }) }
                .distinctUntilChanged()

        override suspend fun getTask(id: String): Task =
            requireNotNull(state.value.find { task -> task.id == id })

        override suspend fun createTask(taskTemplate: TaskTemplate) {
            state.value = state.value.plus(
                Task(
                    id = UUID.randomUUID().toString(),
                    type = taskTemplate.type,
                    title = taskTemplate.title,
                    description = taskTemplate.description,
                    markers = Markers(taskTemplate.isUrgent, taskTemplate.isImportant),
                    isDone = false
                )
            )
        }

        override suspend fun updateTask(task: Task) {
            state.value = state.value.map { listTask ->
                if (listTask.id == task.id) {
                    task
                } else {
                    listTask
                }
            }
        }

        override suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>) {}

        override suspend fun deleteChange(id: Long) {}
    }
