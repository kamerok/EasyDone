package easydone.feature.quickcreatetask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        createView.setOnClickListener {
            GlobalScope.launch {
                repository.createTask(descriptionView.text.toString(), "", false)
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