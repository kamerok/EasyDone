package easydone.feature.createtask

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import easydone.core.domain.DomainRepository
import easydone.coreui.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_create_task.*
import kotlinx.coroutines.launch


class CreateTaskFragment : Fragment(R.layout.fragment_create_task) {

    private lateinit var repository: DomainRepository
    private lateinit var navigator: CreateTaskNavigator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleView.showKeyboard()
        titleView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
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
        if (titleView.text.isEmpty()) {
            titleView.error = "Empty"
            return
        }
        lifecycleScope.launch {
            repository.createTask(
                titleView.text.toString(),
                descriptionView.text.toString(),
                skipInboxView.isChecked
            )
            navigator.closeScreen()
        }
    }

    data class Dependencies(
        val repository: DomainRepository,
        val navigator: CreateTaskNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = CreateTaskFragment().apply {
            repository = dependencies.repository
            navigator = dependencies.navigator
        }
    }

}
