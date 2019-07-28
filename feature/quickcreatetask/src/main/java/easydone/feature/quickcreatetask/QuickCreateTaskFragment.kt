package easydone.feature.quickcreatetask

import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import easydone.coreui.utils.hideKeyboard
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
        view.setOnClickListener { closeScreen() }
        backgroundView.setOnClickListener { }
        descriptionView.apply {
            setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        saveTask()
                        return true
                    }
                    return false
                }
            })
            doOnTextChanged { _, _, _, _ -> updateCreateViewState() }
            imeOptions = EditorInfo.IME_ACTION_DONE
            setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        }
        createView.setOnClickListener { saveTask() }
        updateCreateViewState()
    }

    private fun closeScreen() {
        descriptionView.hideKeyboard()
        //to prevent keyboard from blinking
        Handler().postDelayed({
            navigator.closeScreen()
        }, KEYBOARD_WAIT_DELAY)
    }

    private fun updateCreateViewState() {
        createView.isVisible = !descriptionView.text.isNullOrEmpty()
    }

    private fun saveTask() {
        val title = descriptionView.text.toString()
        if (title.isEmpty()) {
            closeScreen()
        } else {
            GlobalScope.launch {
                repository.createTask(title, "", false)
                closeScreen()
            }
        }
    }

    data class Dependencies(
        val repository: DomainRepository,
        val navigator: QuickCreateTaskNavigator
    )

    companion object {
        private const val KEYBOARD_WAIT_DELAY = 200L

        fun create(dependencies: Dependencies): Fragment = QuickCreateTaskFragment().apply {
            repository = dependencies.repository
            navigator = dependencies.navigator
        }
    }

}