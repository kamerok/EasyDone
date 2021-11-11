package easydone.feature.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate


internal class HomeViewModel : ViewModel() {

    private fun task(i: Int = 0) = UiTask("id:$i", "Task $i", false, false, false)

    val state: StateFlow<State> = MutableStateFlow(State(
        inboxCount = 5,
        todoTasks = (0..10).map { task(it) },
        nextWaitingTask = task() to LocalDate.now().plusDays(10),
        waitingCount = 10,
        maybeTasks = (10..20).map { task(it) }
    ))

    fun onAdd() {}

    fun onSort() {}

    fun onTaskClick(task: UiTask) {}

    fun onWaitingMore() {}

}
