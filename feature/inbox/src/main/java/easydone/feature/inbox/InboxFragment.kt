package easydone.feature.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import easydone.core.domain.DomainRepository


class InboxFragment(
    private val repository: DomainRepository,
    private val navigator: InboxNavigator
) : Fragment() {

    private val viewModel: InboxViewModel by viewModels(factoryProducer = {
        viewModelFactory {
            initializer {
                InboxViewModel(repository, navigator)
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            InboxScreen(viewModel)
        }
    }

}
