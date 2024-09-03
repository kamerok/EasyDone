package easydone.core.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import easydone.core.database.model.DbTaskType
import easydone.core.database.model.EntityField
import easydone.core.database.model.EntityName
import easydone.core.domain.model.Task.Type
import easydone.core.domain.model.Task.Type.ToDo
import easydone.core.domain.model.TaskTemplate
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class DatabaseLocalDataSourceTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        Database.Schema.create(it)
    }
    private val database = DatabaseFactory.create(driver)
    private val dataSource = DatabaseLocalDataSource(database)

    private val taskQueries = database.taskQueries
    private val changesQueries = database.changesQueries

    @Test
    fun `Create simple todo task`() = runBlocking {
        dataSource.createTask(
            TaskTemplate.create(
                type = ToDo,
                title = "Test",
                description = "Task",
                isImportant = false,
                isUrgent = false
            ).getOrThrow()
        )

        val dbTask = taskQueries.selectAll().executeAsOne()
        val id = dbTask.id
        assertThat(dbTask).isEqualTo(
            Task(
                id = id,
                type = DbTaskType.TO_DO,
                title = "Test",
                description = "Task",
                due_date = null,
                is_done = false,
                is_important = false,
                is_urgent = false
            )
        )

        val change = changesQueries.selectChange(EntityName.TASK, id).executeAsOne()
        val delta = changesQueries.selectAllDelta(change.id).executeAsList()
        assertThat(delta).containsExactlyInAnyOrder(
            Delta(change.id, EntityField.TYPE, null, "TO_DO"),
            Delta(change.id, EntityField.TITLE, null, "Test"),
            Delta(change.id, EntityField.DESCRIPTION, null, "Task"),
            Delta(change.id, EntityField.MARKERS, null, "00"),
        )
    }

    @Test
    fun `Create waiting task`() = runBlocking {
        dataSource.createTask(
            TaskTemplate.create(
                type = Type.Waiting(LocalDate.of(2022, Month.AUGUST, 22)),
                title = "Test",
                description = "Task",
                isImportant = false,
                isUrgent = false
            ).getOrThrow()
        )

        val dbTask = taskQueries.selectAll().executeAsOne()
        val id = dbTask.id
        assertThat(dbTask).isEqualTo(
            Task(
                id = id,
                type = DbTaskType.WAITING,
                title = "Test",
                description = "Task",
                due_date = LocalDate.of(2022, Month.AUGUST, 22),
                is_done = false,
                is_important = false,
                is_urgent = false
            )
        )

        val change = changesQueries.selectChange(EntityName.TASK, id).executeAsOne()
        val delta = changesQueries.selectAllDelta(change.id).executeAsList()
        assertThat(delta).containsExactlyInAnyOrder(
            Delta(change.id, EntityField.TYPE, null, "WAITING"),
            Delta(change.id, EntityField.DUE_DATE, null, "2022-08-22"),
            Delta(change.id, EntityField.TITLE, null, "Test"),
            Delta(change.id, EntityField.DESCRIPTION, null, "Task"),
            Delta(change.id, EntityField.MARKERS, null, "00"),
        )
    }

    @Test
    fun `Update todo to waiting`() = runBlocking {
        taskQueries.insert(
            id = "id",
            type = DbTaskType.TO_DO,
            title = "Test",
            description = "Task",
            due_date = null,
            is_urgent = false,
            is_important = false,
            is_done = false
        )

        dataSource.updateTask(
            dataSource.getTask("id").copy(
                type = Type.Waiting(
                    LocalDate.of(2022, Month.AUGUST, 22)
                )
            )
        )

        val change = changesQueries.selectChange(EntityName.TASK, "id").executeAsOne()
        val delta = changesQueries.selectAllDelta(change.id).executeAsList()
        assertThat(delta).containsExactlyInAnyOrder(
            Delta(change.id, EntityField.TYPE, "TO_DO", "WAITING"),
            Delta(change.id, EntityField.DUE_DATE, null, "2022-08-22"),
        )
    }
}
