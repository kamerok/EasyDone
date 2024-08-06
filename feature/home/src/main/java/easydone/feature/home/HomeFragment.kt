package easydone.feature.home

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
import easydone.core.domain.SyncScheduler


class HomeFragment(
    private val syncScheduler: SyncScheduler,
    private val domainRepository: DomainRepository,
    private val navigator: HomeNavigator
) : Fragment() {

    private val viewModel: HomeViewModel by viewModels(factoryProducer = {
        viewModelFactory {
            initializer {
                HomeViewModel(syncScheduler, domainRepository, navigator)
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent { HomeScreen(viewModel) }
    }

}
