package easydone.feature.waiting

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


class WaitingFragment(
    private val repository: DomainRepository,
    private val navigator: WaitingNavigator
) : Fragment() {

    private val viewModel: WaitingViewModel by viewModels(factoryProducer = {
        viewModelFactory {
            initializer {
                WaitingViewModel(repository, navigator)
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            WaitingScreen(viewModel)
        }
    }

}
