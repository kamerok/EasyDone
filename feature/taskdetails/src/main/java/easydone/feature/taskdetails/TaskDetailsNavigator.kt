package easydone.feature.taskdetails

import easydone.core.domain.model.Task
import java.time.LocalDate


interface TaskDetailsNavigator {

    suspend fun selectType(currentType: Task.Type?, date: LocalDate?): Pair<Task.Type, LocalDate?>?

    fun editTask(id: String)

    fun close()

}
