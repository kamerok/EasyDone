package easydone.feature.createtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import easydone.coreui.utils.showKeyboard
import kotlinx.android.synthetic.main.fragment_create_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CreateTaskFragment : Fragment() {

    private lateinit var repository: DomainRepository
    private lateinit var navigator: CreateTaskNavigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().showKeyboard()
        createView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                repository.createTask(
                    titleView.text.toString(),
                    descriptionView.text.toString(),
                    skipInboxView.isChecked
                )
                navigator.closeScreen()
            }
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