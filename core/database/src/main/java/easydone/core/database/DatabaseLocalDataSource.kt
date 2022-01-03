package easydone.core.database

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import easydone.core.database.model.ChangeEntry
import easydone.core.database.model.DbTaskType
import easydone.core.database.model.EntityField
import easydone.core.database.model.EntityName
import easydone.core.domain.LocalDataSource
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID
import kotlin.reflect.KClass
import easydone.core.database.Task as DbTask


class DatabaseLocalDataSource(driver: SqlDriver) : LocalDataSource {

    private val database: Database = Database(
        driver,
        Change.Adapter(EnumColumnAdapter()),
        Delta.Adapter(EnumColumnAdapter()),
        DbTask.Adapter(EnumColumnAdapter(), DateColumnAdapter)
    ).apply { pragmaQueries.forceForeignKeys() }
    private val taskQueries = database.taskQueries
    private val changesQueries = database.changesQueries

    override suspend fun getChanges(): List<TaskDelta> = withContext(Dispatchers.IO) {
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
                ).toDelta()
            }
    }

    override fun observeChangesCount(): Flow<Long> =
        changesQueries.selectChangesCount().asFlow().map { it.executeAsOne() }

    override fun observeTasks(): Flow<List<Task>> =
        taskQueries.selectAll()
            .asFlow()
            .map { it.executeAsList() }
            .map { dbTasks -> dbTasks.map { it.toTask() } }

    override fun observeTasks(type: KClass<out Task.Type>): Flow<List<Task>> =
        taskQueries.selectByType(getDbType(type))
            .asFlow()
            .map { it.executeAsList() }
            .map { dbTasks -> dbTasks.map { it.toTask() } }

    override fun observeTask(id: String): Flow<Task> =
        taskQueries.selectById(id)
            .asFlow()
            .map { it.executeAsOne().toTask() }

    override suspend fun getTask(id: String): Task = withContext(Dispatchers.IO) {
        taskQueries.selectById(id).executeAsOne().toTask()
    }

    override suspend fun createTask(taskTemplate: TaskTemplate) = withContext(Dispatchers.IO) {
        database.transaction {
            val id = UUID.randomUUID().toString()
            val type = getDbType(taskTemplate.type::class)
            taskQueries.insert(
                id = id,
                type = type,
                title = taskTemplate.title,
                description = taskTemplate.description,
                due_date = (taskTemplate.type as? Task.Type.Waiting)?.date,
                is_urgent = taskTemplate.isUrgent,
                is_important = taskTemplate.isImportant,
                is_done = false
            )
            changesQueries.apply {
                insertChange(EntityName.TASK, id)
                val changeId = lastInsertedRow().executeAsOne()
                insertCreateDelta(changeId, EntityField.TYPE, type.name)
                insertCreateDelta(changeId, EntityField.TITLE, taskTemplate.title)
                if (taskTemplate.description.isNotEmpty()) {
                    insertCreateDelta(changeId, EntityField.DESCRIPTION, taskTemplate.description)
                }
                insertCreateDelta(
                    changeId,
                    EntityField.MARKERS,
                    EntityField.MARKERS.getMapper()
                        .toString(Markers(taskTemplate.isUrgent, taskTemplate.isImportant))
                )
                val templateType = taskTemplate.type
                if (templateType is Task.Type.Waiting) {
                    insertCreateDelta(
                        changeId,
                        EntityField.DUE_DATE,
                        EntityField.DUE_DATE.getMapper().toString(templateType.date)
                    )
                }
            }
        }
    }

    override suspend fun updateTask(task: Task) = transaction { updateTaskAndWriteDelta(task) }

    override suspend fun refreshData(tasks: List<Task>, updatedTasks: List<Task>) {
        transaction {
            taskQueries.clear()
            tasks.forEach { insertTask(it) }
            updatedTasks.forEach { updateTaskAndWriteDelta(it) }
        }
    }

    override suspend fun deleteChange(id: Long) = withContext(Dispatchers.IO) {
        changesQueries.deleteChange(id)
    }

    private suspend fun transaction(body: LocalDataSource.() -> Unit) =
        withContext(Dispatchers.IO) {
            database.transaction { body() }
        }

    private fun insertTask(task: Task) = taskQueries.insert(
        id = task.id,
        type = getDbType(task.type::class),
        title = task.title,
        description = task.description,
        due_date = (task.type as? Task.Type.Waiting)?.date,
        is_urgent = task.markers.isUrgent,
        is_important = task.markers.isImportant,
        is_done = task.isDone
    )

    private fun updateTaskAndWriteDelta(task: Task) {
        val id = task.id
        val oldTask = taskQueries.selectById(id).executeAsOne().toTask()
        taskQueries.update(
            type = getDbType(task.type::class),
            title = task.title,
            description = task.description,
            due_date = (task.type as? Task.Type.Waiting)?.date,
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
                        updateDeltaValue(newValue, changeId, field)
                    } else {
                        deleteDelta(changeId, field)
                    }
                }
            }
            writeDelta(EntityField.TYPE) { getDbType(type::class) }
            writeDelta(EntityField.TITLE) { title }
            writeDelta(EntityField.DESCRIPTION) { description }
            writeDelta(EntityField.DUE_DATE) { (type as? Task.Type.Waiting)?.date }
            writeDelta(EntityField.MARKERS) { markers }
            writeDelta(EntityField.IS_DONE) { isDone }

            if (selectDeltaCount(changeId).executeAsOne() == 0L) {
                deleteChange(changeId)
            }
        }
    }

    private fun getDbType(type: KClass<out Task.Type>): DbTaskType =
        when (type) {
            Task.Type.Inbox::class -> DbTaskType.INBOX
            Task.Type.ToDo::class -> DbTaskType.TO_DO
            Task.Type.Waiting::class -> DbTaskType.WAITING
            Task.Type.Project::class -> DbTaskType.PROJECT
            Task.Type.Maybe::class -> DbTaskType.MAYBE
            else -> throw IllegalArgumentException("Unknown type")
        }

    private fun DbTask.toTask() = Task(
        id = id,
        type = type.toType(due_date),
        title = title,
        description = description,
        markers = Markers(is_urgent, is_important),
        isDone = is_done
    )
}

internal fun ChangeEntry.toDelta() = TaskDelta(
    id = changeId,
    taskId = entityId,
    type = newType(),
    title = fields[EntityField.TITLE] as String?,
    description = fields[EntityField.DESCRIPTION] as String?,
    markers = fields[EntityField.MARKERS] as Markers?,
    isDone = fields[EntityField.IS_DONE] as Boolean?
)

private fun ChangeEntry.newType(): Task.Type? {
    val newDbType = fields[EntityField.TYPE] as DbTaskType?
    val newDate = fields[EntityField.DUE_DATE] as LocalDate?
    return when {
        newDbType != null -> newDbType.toType(newDate)
        newDate != null -> Task.Type.Waiting(newDate)
        else -> null
    }
}

private fun DbTaskType.toType(date: LocalDate?): Task.Type = when (this) {
    DbTaskType.INBOX -> Task.Type.Inbox
    DbTaskType.TO_DO -> Task.Type.ToDo
    DbTaskType.WAITING -> Task.Type.Waiting(requireNotNull(date))
    DbTaskType.PROJECT -> Task.Type.Project
    DbTaskType.MAYBE -> Task.Type.Maybe
}
