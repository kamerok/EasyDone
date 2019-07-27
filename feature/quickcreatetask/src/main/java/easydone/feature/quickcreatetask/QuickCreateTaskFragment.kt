package easydone.feature.quickcreatetask

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import kotlinx.android.synthetic.main.fragment_quick_create_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class QuickCreateTaskFragment : Fragment() {

    private lateinit var repository: DomainRepository
    private lateinit var navigator: QuickCreateTaskNavigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_quick_create_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        descriptionView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveTask()
                    return true
                }
                return false
            }
        })
        createView.setOnClickListener { saveTask() }
    }

    private fun saveTask() {
        val title = descriptionView.text.toString()
        if (title.isEmpty()) {
            navigator.closeScreen()
        } else {
            GlobalScope.launch {
                repository.createTask(title, "", false)
                navigator.closeScreen()
            }
        }
    }

    data class Dependencies(
        val repository: DomainRepository,
        val navigator: QuickCreateTaskNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = QuickCreateTaskFragment().apply {
            repository = dependencies.repository
            navigator = dependencies.navigator
        }
    }

}