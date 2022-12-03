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
import java.time.LocalDate
import java.util.UUID
import kotlin.reflect.KClass

class SandboxLocalDataSource : LocalDataSource {
    private val state: MutableStateFlow<List<Task>> = MutableStateFlow(
        listOf(
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Inbox,
                title = "Mark advised to see Intouchables",
                description = "",
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Inbox,
                title = "Develop an app",
                description = "",
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.ToDo,
                title = "Pay for the electric bill",
                description = "[Company site](google.com)",
                markers = Markers(isUrgent = true, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.ToDo,
                title = "Make an appointment with the dentist",
                description = "",
                markers = Markers(isUrgent = false, isImportant = true),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Project,
                title = "Health checkup",
                description = "",
                markers = Markers(isUrgent = false, isImportant = true),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Waiting(LocalDate.now().plusDays(17)),
                title = "Pay rent",
                description = "",
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Waiting(LocalDate.now().plusDays(17)),
                title = "Pay electricity",
                description = "",
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Waiting(LocalDate.now().plusDays(29)),
                title = "Take cat to the vet",
                description = "",
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Waiting(LocalDate.now().plusDays(58)),
                title = "Plan vacation",
                description = "",
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Waiting(LocalDate.now().plusDays(58)),
                title = "Research investment opportunities",
                description = "",
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Maybe,
                title = "Take an art class",
                description = "[Link](google.com)",
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
            Task(
                id = UUID.randomUUID().toString(),
                type = Task.Type.Maybe,
                title = "Write an article about differences between reactive libraries",
                description = """
                    Ideas:
                    - idea one
                    - idea two
                """.trimIndent(),
                markers = Markers(isUrgent = false, isImportant = false),
                isDone = false
            ),
        )
    )

    override suspend fun getChanges(): List<TaskDelta> = emptyList()

    override fun observeChangesCount(): Flow<Long> = flowOf(0)

    override fun observeTasks(): Flow<List<Task>> =
        state
            .map { it.filter { task -> !task.isDone } }
            .distinctUntilChanged()

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
