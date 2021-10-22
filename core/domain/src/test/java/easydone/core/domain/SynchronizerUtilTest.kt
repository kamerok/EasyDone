package easydone.core.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import easydone.core.domain.model.Task.Type.INBOX
import easydone.core.domain.model.Task.Type.MAYBE
import easydone.core.domain.model.Task.Type.TO_DO
import easydone.core.domain.model.Task.Type.WAITING
import org.junit.Test
import java.time.LocalDate
import java.util.UUID

class SynchronizerUtilTest {

    private val today = LocalDate.of(2020, 1, 10)
    private val yesterday = today.minusDays(1)
    private val tomorrow = today.plusDays(1)

    @Test
    fun `No updates required when no dates and waiting is in the future`() {
        val input = listOf(task(INBOX), task(WAITING, tomorrow), task(TO_DO), task(MAYBE))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).isEmpty()
    }

    @Test
    fun `Clear task date for inbox if task date is due`() {
        val input = listOf(task(INBOX, yesterday))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(INBOX)
        assertThat(output.first().dueDate).isNull()
    }

    @Test
    fun `Move task from inbox to waiting if task has due date`() {
        val input = listOf(task(INBOX, tomorrow))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(WAITING)
        assertThat(output.first().dueDate).isEqualTo(tomorrow)
    }

    @Test
    fun `Clear task date for to_do task if task date is due`() {
        val input = listOf(task(TO_DO, yesterday))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(TO_DO)
        assertThat(output.first().dueDate).isNull()
    }

    @Test
    fun `Move task from to_do to waiting if task has due date`() {
        val input = listOf(task(TO_DO, tomorrow))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(WAITING)
        assertThat(output.first().dueDate).isEqualTo(tomorrow)
    }

    @Test
    fun `Clear task date for maybe task if task date is due`() {
        val input = listOf(task(MAYBE, yesterday))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(MAYBE)
        assertThat(output.first().dueDate).isNull()
    }

    @Test
    fun `Move task from maybe to waiting if task has due date`() {
        val input = listOf(task(MAYBE, tomorrow))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(WAITING)
        assertThat(output.first().dueDate).isEqualTo(tomorrow)
    }

    @Test
    fun `Move waiting task to inbox if date is due`() {
        val input = listOf(task(WAITING, yesterday))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(INBOX)
        assertThat(output.first().dueDate).isNull()
    }

    @Test
    fun `Move waiting task to inbox if date is null`() {
        val input = listOf(task(WAITING))

        val output = Synchronizer.updateWaitingTasks(input, today)

        assertThat(output).hasSize(1)
        assertThat(output.first().type).isEqualTo(INBOX)
        assertThat(output.first().dueDate).isNull()
    }

    private fun task(type: Task.Type, dueDate: LocalDate? = null) = Task(
        id = UUID.randomUUID().toString(),
        type = type,
        title = "",
        description = "",
        dueDate = dueDate,
        markers = Markers(isUrgent = false, isImportant = false),
        isDone = false
    )
}
