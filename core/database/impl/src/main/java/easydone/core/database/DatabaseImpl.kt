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
    private val database: Database = Database(
        driver,
        Change.Adapter(EnumColumnAdapter()),
        Delta.Adapter(EnumColumnAdapter()),
        DbTask.Adapter(EnumColumnAdapter())
    ).apply { pragmaQueries.forceForeignKeys() }
    private val taskQueries = database.taskQueries
    private val changesQueries = database.changesQueries

    override suspend fun getChanges(): List<ChangeEntry> =
        changesQueries.selectChanges().executeAsList()
            .groupBy { it.id }
            .map { entry ->
                ChangeEntry(
                    entry.key,
                    entry.value.first().entity_name,
                    entry.value.first().entity_id,
                    entry.value.associate { it.field to it.field.getMapper().toValue(it.new_value) }
                )
            }

    override suspend fun deleteChange(id: Long) = changesQueries.deleteChange(id)

    override fun getTasks(type: Task.Type): Flow<List<Task>> =
        taskQueries.selectByType(type).toFlow().map { dbTasks -> dbTasks.map { it.toTask() } }

    override suspend fun getTask(id: String): Task =
        taskQueries.selectById(id).executeAsOne().toTask()

    override suspend fun createTask(taskTemplate: TaskTemplate) = database.transaction {
        val id = UUID.randomUUID().toString()
        taskQueries.insert(
            id,
            taskTemplate.type,
            taskTemplate.title,
            taskTemplate.description,
            false
        )
        changesQueries.apply {
            insertChange(EntityName.TASK, id)
            val changeId = lastInsertedRow().executeAsOne()
            insertCreateDelta(changeId, EntityField.TYPE, taskTemplate.type.name)
            insertCreateDelta(changeId, EntityField.TITLE, taskTemplate.title)
            if (taskTemplate.description.isNotEmpty()) {
                insertCreateDelta(changeId, EntityField.DESCRIPTION, taskTemplate.description)
            }
        }
    }

    override suspend fun updateTask(task: Task) = database.transaction {
        val id = task.id
        val oldTask = taskQueries.selectById(id).executeAsOne().toTask()
        taskQueries.update(task.type, task.title, task.description, task.isDone, id)

        changesQueries.apply {
            val existingChange = selectChange(EntityName.TASK, id).executeAsOneOrNull()
            val changeId = if (existingChange != null) {
                existingChange.id
            } else {
                insertChange(EntityName.TASK, id)
                lastInsertedRow().executeAsOne()
            }

            fun <T : Any> writeDelta(field: EntityField, getField: Task.() -> T) {
                val mapper = field.getMapper()
                val previousValue = mapper.toString(oldTask.getField())
                val newValue = mapper.toString(task.getField())
                if (newValue == previousValue) return
                val oldDelta = selectDelta(changeId, field).executeAsOneOrNull()

                if (oldDelta == null) {
                    insertUpdateDelta(changeId, field, previousValue, newValue)
                } else {
                    if (newValue != oldDelta.old_value) {
                        updateDelta(newValue, changeId, field)
                    } else {
                        deleteDelta(changeId, field)
                    }
                }
            }
            writeDelta(EntityField.TYPE) { type }
            writeDelta(EntityField.TITLE) { title }
            writeDelta(EntityField.DESCRIPTION) { description }
            writeDelta(EntityField.IS_DONE) { isDone }

            if (selectDeltaCount(changeId).executeAsOne() == 0L) {
                deleteChange(changeId)
            }
        }
    }

    override suspend fun putData(tasks: List<Task>) {
        database.transaction {
            taskQueries.clear()
            tasks.forEach {
                taskQueries.insert(it.id, it.type, it.title, it.description, it.isDone)
            }
        }
    }

    override suspend fun clear() = database.taskQueries.clear()
}

fun DbTask.toTask() = Task(id, type, title, description, is_done)

fun <T : Any> Query<T>.toFlow() = flow {
    asObservable().openSubscription().consumeEach { emit(it.executeAsList()) }
}