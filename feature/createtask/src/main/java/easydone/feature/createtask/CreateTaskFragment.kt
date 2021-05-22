package easydone.feature.createtask

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import easydone.core.domain.DomainRepository
import easydone.coreui.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_create_task.*
import kotlinx.coroutines.launch


class CreateTaskFragment(
    private val repository: DomainRepository,
    private val navigator: CreateTaskNavigator
) : Fragment(R.layout.fragment_create_task) {

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

        view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            insets.consumeSystemWindowInsets()
        }
    }

    private fun saveTask() {
        if (titleView.text.isEmpty()) {
            titleView.error = "Empty"
            return
        }
        lifecycleScope.launch {
            repository.createTask(
                title = titleView.text.toString(),
                description = descriptionView.text.toString(),
                skipInbox = skipInboxView.isChecked,
                isUrgent = false,
                isImportant = false
            )
            navigator.closeScreen()
        }
    }

}
