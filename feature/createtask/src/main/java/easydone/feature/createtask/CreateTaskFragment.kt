package easydone.feature.createtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import kotlinx.android.synthetic.main.fragment_create_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreateTaskFragment : Fragment() {

    private lateinit var repository: DomainRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_create_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createView.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
               repository.createTask(titleView.text.toString(), skipInboxView.isChecked)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "done", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class Dependencies(
        var repository: DomainRepository
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = CreateTaskFragment().apply {
            repository = dependencies.repository
        }
    }

}