package easydone.core.database

import android.app.Application
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.rx.asObservable
import easydone.core.model.Task
import easydone.core.model.TaskTemplate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.openSubscription
import java.util.UUID
import easydone.core.database.Task as DbTask


@ExperimentalCoroutinesApi
class DatabaseImpl(application: Application) : MyDatabase {

    private val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, application, "database.db")
    private val database: Database = Database(driver, DbTask.Adapter(EnumColumnAdapter()))
    private val taskQueries = database.taskQueries

    private val changeLog = mutableListOf<Pair<Action, Task>>()

    override fun getTasks(type: Task.Type): Flow<List<Task>> =
        taskQueries.selectByType(type).toFlow().mapToDomain()

    override suspend fun getChanges(): List<Pair<Action, Task>> = changeLog

    override suspend fun getTask(id: String): Task =
        taskQueries.selectById(id).executeAsOne().toTask()

    override suspend fun createTask(taskTemplate: TaskTemplate) {
        val id = UUID.randomUUID().toString()
        changeLog.add(Action.CREATE to taskTemplate.toTask(id))
    }

    override suspend fun updateTask(task: Task) {
        changeLog.add(Action.UPDATE to task)
    }

    override suspend fun putData(tasks: List<Task>) {
        changeLog.clear()
        taskQueries.clear()
        tasks.forEach {
            taskQueries.insert(it.id, it.type, it.title, it.description, it.isDone)
        }
    }

    override suspend fun clear() = database.taskQueries.clear()
}

fun DbTask.toTask() = Task(
    id, type, title, description, is_done
)

fun <T : Any> Query<T>.toFlow() = flow {
    asObservable().openSubscription().consumeEach { emit(it.executeAsList()) }
}

fun Flow<List<DbTask>>.mapToDomain() = map { it.map { it.toTask() } }