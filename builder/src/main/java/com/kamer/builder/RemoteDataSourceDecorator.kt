package com.kamer.builder

import easydone.core.domain.RemoteDataSource
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.sandbox.SandboxRemoteDataSource

internal class RemoteDataSourceDecorator(defaultSource: RemoteDataSource) : RemoteDataSource {
    private var source: RemoteDataSource = defaultSource

    override suspend fun isConnected(): Boolean = source.isConnected()

    override suspend fun getAllTasks(): List<Task> = source.getAllTasks()

    override suspend fun isTaskKnownOnRemote(id: String): Boolean = source.isTaskKnownOnRemote(id)

    override suspend fun updateTask(delta: TaskDelta) = source.updateTask(delta)

    override suspend fun createTask(delta: TaskDelta) = source.createTask(delta)

    override suspend fun disconnect() = source.disconnect()

    fun switchToSandbox() {
        source = SandboxRemoteDataSource()
    }
}
