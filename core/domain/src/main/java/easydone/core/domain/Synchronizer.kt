package easydone.core.domain

import easydone.core.domain.model.Task
import java.time.LocalDate


class Synchronizer(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) {

    suspend fun sync() {
        // get remote data
        // sync change and apply it to the remote data
        // also sync waiting if needed (this renders flushing new delta unneeded)
        // flush remote data to local
        uploadChanges()
        refreshLocalData()
    }

    private suspend fun uploadChanges() {
        localDataSource.getChanges().forEach { change ->
            if (remoteDataSource.isTaskKnownOnRemote(change.taskId)) {
                remoteDataSource.updateTask(change)
            } else {
                remoteDataSource.createTask(change)
            }
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
