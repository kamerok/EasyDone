package easydone.feature.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import kotlinx.android.synthetic.main.fragment_edit_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditTaskFragment : Fragment() {

    private lateinit var id: String
    private lateinit var repository: DomainRepository
    private lateinit var navigator: EditTaskNavigator

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
                descriptionView.text = task.description
            }
        }
        editView.setOnClickListener {
            isEdit = !isEdit
            descriptionView.isVisible = !isEdit
            editDescriptionView.isVisible = isEdit
            editView.text = if (isEdit) "Cancel" else "Edit"
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