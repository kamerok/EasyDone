package easydone.core.database

import android.app.Application
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import easydone.core.domain.database.ChangeEntry
import easydone.core.domain.database.EntityField
import easydone.core.domain.database.EntityName
import easydone.core.domain.database.MyDatabase
import easydone.core.model.Markers
import easydone.core.model.Task
import easydone.core.model.TaskTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import easydone.core.database.Task as DbTask


@ExperimentalCoroutinesApi
class DatabaseImpl(application: Application) : MyDatabase {

    private val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, application, "database.db")
    private val database: Database = Database(
        driver,
        Change.Adapter(EnumColumnAdapter()),
        Delta.Adapter(EnumColumnAdapter()),
        DbTask.Adapter(EnumColumnAdapter(), DateColumnAdapter)
    ).apply { pragmaQueries.forceForeignKeys() }
    private val taskQueries = database.taskQueries
    private val changesQueries = database.changesQueries

    override suspend fun getChanges(): List<ChangeEntry> = withContext(Dispatchers.IO) {
        changesQueries.selectChanges().executeAsList()
            .groupBy { it.id }
            .map { entry ->
                ChangeEntry(
                    entry.key,
                    entry.value.first().entity_name,
                    entry.value.first().entity_id,
                    entry.value.associate {
                        it.field_ to it.field_.getMapper().toValue(it.new_value)
                    }
                )
            }
    }

    override fun observeChangesCount(): Flow<Long> =
        changesQueries.selectChangesCount().asFlow().map { it.executeAsOne() }

    override suspend fun deleteChange(id: Long) = withContext(Dispatchers.IO) {
        changesQueries.deleteChange(id)
    }

    override fun getTasksStream(type: Task.Type): Flow<List<Task>> =
        taskQueries.selectByType(type)
            .asFlow()
            .map { it.executeAsList() }
            .map { dbTasks -> dbTasks.map { it.toTask() } }

    override fun getTasks(type: Task.Type): List<Task> =
        taskQueries.selectByType(type).executeAsList().map { it.toTask() }

    override suspend fun getTask(id: String): Task = withContext(Dispatchers.IO) {
        taskQueries.selectById(id).executeAsOne().toTask()
    }

    override suspend fun createTask(taskTemplate: TaskTemplate) = withContext(Dispatchers.IO) {
        database.transaction {
            val id = UUID.randomUUID().toString()
            taskQueries.insert(
                id = id,
                type = taskTemplate.type,
                title = taskTemplate.title,
                description = taskTemplate.description,
                due_date = null,
                is_urgent = taskTemplate.isUrgent,
                is_important = taskTemplate.isImportant,
                is_done = false
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
    }

    override fun updateTask(task: Task) {
        val id = task.id
        val oldTask = taskQueries.selectById(id).executeAsOne().toTask()
        taskQueries.update(
            type = task.type,
            title = task.title,
            description = task.description,
            due_date = task.dueDate,
            is_urgent = task.markers.isUrgent,
            is_important = task.markers.isImportant,
            is_done = task.isDone,
            id = id
        )

        changesQueries.apply {
            val existingChange = selectChange(EntityName.TASK, id).executeAsOneOrNull()
            val changeId = if (existingChange != null) {
                existingChange.id
            } else {
                insertChange(EntityName.TASK, id)
                lastInsertedRow().executeAsOne()
            }

            fun <T : Any> writeDelta(field: EntityField, getField: Task.() -> T?) {
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
            writeDelta(EntityField.DUE_DATE) { dueDate }
            writeDelta(EntityField.MARKERS) { markers }
            writeDelta(EntityField.IS_DONE) { isDone }

            if (selectDeltaCount(changeId).executeAsOne() == 0L) {
                deleteChange(changeId)
            }
        }
    }

    override fun putData(tasks: List<Task>) = tasks.forEach {
        taskQueries.insert(
            id = it.id,
            type = it.type,
            title = it.title,
            description = it.description,
            due_date = it.dueDate,
            is_urgent = it.markers.isUrgent,
            is_important = it.markers.isImportant,
            is_done = it.isDone
        )
    }

    override fun clear() = taskQueries.clear()

    override fun getTasksWithDate(): List<Task> =
        taskQueries.selectWithDate().executeAsList().map { it.toTask() }

    override suspend fun transaction(body: MyDatabase.() -> Unit) = withContext(Dispatchers.IO) {
        database.transaction { body() }
    }
}

fun DbTask.toTask() =
    Task(id, type, title, description, due_date, Markers(is_urgent, is_important), is_done)
