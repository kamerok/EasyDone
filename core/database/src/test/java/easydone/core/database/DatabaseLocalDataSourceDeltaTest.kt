package easydone.core.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import easydone.core.database.model.ChangeEntry
import easydone.core.database.model.DbTaskType
import easydone.core.database.model.EntityField
import easydone.core.database.model.EntityName
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskDelta
import org.junit.Test
import java.time.LocalDate

class DatabaseLocalDataSourceDeltaTest {

    private val tomorrow = LocalDate.now().plusDays(1)

    @Test
    fun `Inbox to maybe`() {
        val delta = change(
            mapOf(
                EntityField.TYPE to DbTaskType.MAYBE
            )
        ).toDelta()

        assertThat(delta).isEqualTo(delta(Task.Type.Maybe))
    }

    @Test
    fun `Inbox to waiting`() {
        val delta = change(
            mapOf(
                EntityField.TYPE to DbTaskType.WAITING,
                EntityField.DUE_DATE to tomorrow
            )
        ).toDelta()

        assertThat(delta).isEqualTo(delta(Task.Type.Waiting(tomorrow)))
    }

    @Test
    fun `Waiting to inbox`() {
        val delta = change(
            mapOf(
                EntityField.TYPE to DbTaskType.INBOX,
                EntityField.DUE_DATE to null
            )
        ).toDelta()

        assertThat(delta).isEqualTo(delta(Task.Type.Inbox))
    }

    @Test
    fun `Waiting to waiting`() {
        val delta = change(
            mapOf(
                EntityField.DUE_DATE to tomorrow
            )
        ).toDelta()

        assertThat(delta).isEqualTo(delta(Task.Type.Waiting(tomorrow)))
    }

    private fun change(fields: Map<EntityField, Any?>): ChangeEntry =
        ChangeEntry(
            changeId = 0,
            entityName = EntityName.TASK,
            entityId = "id",
            fields = fields
        )

    private fun delta(type: Task.Type): TaskDelta =
        TaskDelta(
            id = 0,
            taskId = "id",
            type = type,
            title = null,
            description = null,
            markers = null,
            isDone = null
        )
}
