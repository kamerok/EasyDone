package easydone.feature.edittask

import android.app.DatePickerDialog
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.launch
import org.commonmark.node.SoftLineBreak
import org.threeten.bp.LocalDate


class EditTaskFragment(
    private val repository: DomainRepository,
    private val navigator: EditTaskNavigator
) : Fragment(R.layout.fragment_edit_task) {

    private val id: String by lazy { arguments?.getString(TASK_ID) ?: error("ID must be provided") }

    private lateinit var originalTask: Task
    private var date: LocalDate? = null

    private val markwon by lazy {
        Markwon
            .builder(requireContext())
            .usePlugins(
                listOf(
                    object : AbstractMarkwonPlugin() {
                        override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                            builder.on(SoftLineBreak::class.java) { visitor, _ -> visitor.forceNewLine() }
                        }
                    },
                    LinkifyPlugin.create(Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS or Linkify.WEB_URLS)
                )
            )
            .build()
    }

    private var isEdit = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val task = repository.getTask(id)
            originalTask = task
            date = task.dueDate
            titleView.setText(task.title)
            editDescriptionView.setText(task.description)
            markwon.setMarkdown(descriptionView, task.description)
            updateDate()
            labelsView.text = when {
                task.markers.isUrgent && task.markers.isImportant -> "Urgent, Important"
                task.markers.isUrgent -> "Urgent"
                task.markers.isImportant -> "Important"
                else -> ""
            }
        }
        dateView.setOnClickListener {
            val dateCalendar = date ?: LocalDate.now()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    date = LocalDate.of(year, month + 1, dayOfMonth)
                    updateDate()
                },
                dateCalendar.year,
                dateCalendar.monthValue - 1,
                dateCalendar.dayOfMonth
            ).apply {
                if (date != null) {
                    setButton(BUTTON_NEUTRAL, "Clear") { _, _ ->
                        date = null
                        updateDate()
                    }
                }
            }.show()
        }
        editView.setOnClickListener {
            isEdit = !isEdit
            descriptionView.isVisible = !isEdit
            editDescriptionView.isVisible = isEdit
            editView.text = if (isEdit) "Cancel" else "Edit"
            if (!isEdit) {
                markwon.setMarkdown(descriptionView, editDescriptionView.text.toString())
            }
        }
        saveView.setOnClickListener {
            lifecycleScope.launch {
                val newType = when {
                    originalTask.type == Task.Type.WAITING && date == null -> Task.Type.INBOX
                    originalTask.type != Task.Type.WAITING && date != null -> Task.Type.WAITING
                    else -> originalTask.type
                }
                repository.saveTask(
                    Task(
                        id = id,
                        type = newType,
                        title = titleView.text.toString(),
                        description = editDescriptionView.text.toString(),
                        dueDate = date,
                        markers = originalTask.markers,
                        isDone = false
                    )
                )
                navigator.closeScreen()
            }
        }
        archiveView.setOnClickListener {
            lifecycleScope.launch {
                repository.archiveTask(id)
                navigator.closeScreen()
            }
        }
        moveView.setOnClickListener {
            lifecycleScope.launch {
                repository.moveTask(id)
                navigator.closeScreen()
            }
        }
        urgentView.setOnClickListener {
            lifecycleScope.launch {
                repository.switchUrgent(id)
                navigator.closeScreen()
            }
        }
        importantView.setOnClickListener {
            lifecycleScope.launch {
                repository.switchImportant(id)
                navigator.closeScreen()
            }
        }

        view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            insets.consumeSystemWindowInsets()
        }
    }

    private fun updateDate() {
        dateView.text = date?.toString() ?: "Select date"
    }

    companion object {
        const val TASK_ID = "task_id"
    }

}
