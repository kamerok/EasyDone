package easydone.feature.edittask

import easydone.core.domain.model.Task
import java.time.LocalDate


interface EditTaskNavigator {

    fun close()

    suspend fun selectType(currentType: Task.Type?, date: LocalDate?): Pair<Task.Type, LocalDate?>?

}
