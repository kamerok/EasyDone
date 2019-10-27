package easydone.feature.edittask

import android.app.DatePickerDialog
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import easydone.core.domain.DomainRepository
import easydone.core.model.Task
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.launch
import org.commonmark.node.SoftLineBreak
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.Calendar.getInstance
import java.util.Date


class EditTaskFragment(
    private val repository: DomainRepository,
    private val navigator: EditTaskNavigator
) : Fragment(R.layout.fragment_edit_task) {

    private val id: String by lazy { arguments?.getString(TASK_ID) ?: error("ID must be provided") }

    private lateinit var originalTask: Task
    private var date: Date? = null

    private val markwon by lazy {
        Markwon
            .builder(requireContext())
            .usePlugins(
                listOf(
                    object : AbstractMarkwonPlugin() {
                        override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                            builder.on(SoftLineBreak::class.java) { visitor, softLineBreak -> visitor.forceNewLine() }
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
        }
        dateView.setOnClickListener {
            val dateCalendar = getInstance().apply { date?.let { time = it } }
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    date = getInstance().apply {
                        date?.let { time = it }
                        set(year, month, dayOfMonth)
                    }.time
                    updateDate()
                },
                dateCalendar.get(YEAR),
                dateCalendar.get(MONTH),
                dateCalendar.get(DAY_OF_MONTH)
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
                repository.saveTask(
                    Task(
                        id,
                        originalTask.type,
                        titleView.text.toString(),
                        editDescriptionView.text.toString(),
                        date,
                        false
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
    }

    private fun updateDate() {
        dateView.text = date?.toString() ?: "Select date"
    }

    companion object {
        const val TASK_ID = "task_id"
    }

}
