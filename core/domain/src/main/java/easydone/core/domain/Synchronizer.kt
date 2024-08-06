package easydone.core.domain

import easydone.core.domain.model.Task
import java.time.LocalDate


class Synchronizer(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) {

    suspend fun sync() {
        uploadChanges()
        refreshLocalData()
    }

    private suspend fun uploadChanges() {
        localDataSource.getChanges().forEach { change ->
            remoteDataSource.syncTaskDelta(change)
            localDataSource.deleteChange(change.id)
        }
    }

    private suspend fun refreshLocalData() {
        val remoteTasks = remoteDataSource.getAllTasks()
        val updatedTasks = updateWaitingTasks(remoteTasks, LocalDate.now())
        localDataSource.refreshData(remoteTasks, updatedTasks)
    }

    companion object {
        internal fun updateWaitingTasks(tasks: List<Task>, today: LocalDate): List<Task> =
            tasks.mapNotNull { task ->
                if (task.type is Task.Type.Waiting && !task.type.date.isAfter(today)) {
                    task.copy(type = Task.Type.Inbox)
                } else {
                    null
                }
            }
    }
}
