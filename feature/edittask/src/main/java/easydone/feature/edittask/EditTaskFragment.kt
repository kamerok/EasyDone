package easydone.feature.edittask

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import easydone.core.model.Task
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.commonmark.node.SoftLineBreak


class EditTaskFragment : Fragment() {

    private lateinit var id: String
    private lateinit var repository: DomainRepository
    private lateinit var navigator: EditTaskNavigator

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_edit_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        GlobalScope.launch(Dispatchers.IO) {
            val task = repository.getTask(id)
            withContext(Dispatchers.Main) {
                titleView.setText(task.title)
                editDescriptionView.setText(task.description)
                markwon.setMarkdown(descriptionView, task.description)
            }
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
            GlobalScope.launch(Dispatchers.IO) {
                repository.saveTask(
                    Task(
                        id,
                        titleView.text.toString(),
                        editDescriptionView.text.toString()
                    )
                )
                navigator.closeScreen()
            }
        }
        archiveView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                repository.archiveTask(id)
                navigator.closeScreen()
            }
        }
        moveView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                repository.moveTask(id)
                navigator.closeScreen()
            }
        }
    }

    data class Dependencies(
        val id: String,
        val repository: DomainRepository,
        val navigator: EditTaskNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = EditTaskFragment().apply {
            id = dependencies.id
            repository = dependencies.repository
            navigator = dependencies.navigator
        }
    }

}