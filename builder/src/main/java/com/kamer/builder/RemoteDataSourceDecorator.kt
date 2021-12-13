package com.kamer.builder

import easydone.core.domain.RemoteDataSource
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.sandbox.SandboxRemoteDataSource

internal class RemoteDataSourceDecorator(defaultSource: RemoteDataSource) : RemoteDataSource {
        private var source: RemoteDataSource = defaultSource

    override suspend fun isConnected(): Boolean = source.isConnected()

    override suspend fun getAllTasks(): List<Task> = source.getAllTasks()

    override suspend fun syncTaskDelta(delta: TaskDelta) = source.syncTaskDelta(delta)

    override suspend fun disconnect() = source.disconnect()

    fun switchToSandbox() {
            source = SandboxRemoteDataSource()
        }
    }
