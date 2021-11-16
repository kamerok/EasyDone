package com.kamer.builder

import easydone.core.domain.LocalDataSource
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.domain.model.TaskTemplate
import easydone.core.sandbox.SandboxLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

internal class LocalDataSourceDecorator(defaultSource: LocalDataSource) : LocalDataSource {
        private var source: LocalDataSource = defaultSource

        override suspend fun getChanges(): List<TaskDelta> = source.getChanges()

        override fun observeChangesCount(): Flow<Long> = source.observeChangesCount()

        override fun observeTasks(type: KClass<out Task.Type>): Flow<List<Task>> =
            source.observeTasks(type)

        override fun observeTask(id: String): Flow<Task> = source.observeTask(id)

        override suspend fun getTask(id: String): Task = source.getTask(id)

        override suspend fun createTask(taskTemplate: TaskTemplate) =
            source.createTask(taskTemplate)

        override suspend fun updateTask(task: Task) = source.updateTask(task)

        override suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>) =
            source.refreshData(tasks, updatedTasks)

        override suspend fun deleteChange(id: Long) = source.deleteChange(id)

        fun switchToInMemoryDemoDatabase() {
            source = SandboxLocalDataSource()
        }
    }
