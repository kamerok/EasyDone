package easydone.core.database

import android.app.Application
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import easydone.core.model.Task
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import easydone.core.database.Task as DbTask


class DatabaseImpl(application: Application) : MyDatabase {

    private val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, application, "database.db")
    private val database: Database = Database(driver, DbTask.Adapter(EnumColumnAdapter()))
    private val taskQueries = database.taskQueries

    private val channel: BroadcastChannel<List<Task>> =
        ConflatedBroadcastChannel(taskQueries.selectAll().executeAsList().map {
            Task(
                it.id,
                it.type,
                it.title,
                it.description ?: "",
                it.is_done
            )
        })

    private val changeLog = mutableListOf<Pair<Action, Task>>()

    override fun getTasks(type: Task.Type): Flow<List<Task>> = flow {
        channel.consumeEach { tasks -> emit(tasks.filter { it.type == type && !it.isDone }) }
    }

    override suspend fun getChanges(): List<Pair<Action, Task>> = changeLog

    override suspend fun getTask(id: String): Task {
        val tasks = channel.asFlow().first()
        return tasks.find { it.id == id }!!
    }

    override suspend fun createTask(task: Task) {
        changeLog.add(Action.CREATE to task)
        val tasks = channel.asFlow().first()
        channel.send(tasks.plus(task))
    }

    override suspend fun updateTask(task: Task) {
        changeLog.add(Action.UPDATE to task)
        val tasks = channel.asFlow().first()
        channel.send(tasks.map { if (task.id == it.id) task else it })
    }

    override suspend fun putData(tasks: List<Task>) {
        changeLog.clear()
        taskQueries.clear()
        tasks.forEach {
            taskQueries.insert(it.id, it.type, it.title, it.description, it.isDone)
        }
        channel.send(tasks)
    }

    override suspend fun clear() = channel.send(emptyList())
}