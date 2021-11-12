package easydone.core.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import org.junit.Test
import java.time.LocalDate
import java.util.UUID

class SynchronizerUtilTest {

    private val today = LocalDate.of(2020, 1, 10)
    private val yesterday = today.minusDays(1)
    private val tomorrow = today.plusDays(1)

    @Test
    fun `No updates required when no dates and waiting is in the future`() {
        val input = listOf(
            task(Task.Type.Inbox),
            task(Task.Type.Waiting(tomorrow)),
            task(Task.Type.ToDo),
            task(Task.Type.Maybe)
        )

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).isEmpty()
    }

    @Test
    fun `Move waiting task to inbox if date is due`() {
        val input = listOf(task(Task.Type.Waiting(yesterday)))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(Task.Type.Inbox)
    }

    private fun task(type: Task.Type) = Task(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "",
        description = "",
        markers = Markers(isUrgent = false, isImportant = false),
        isDone = false
    )
}
