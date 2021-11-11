package easydone.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import easydone.core.domain.DomainRepository
import easydone.core.domain.Synchronizer


class HomeFragment(
    private val synchronizer: Synchronizer,
    private val domainRepository: DomainRepository,
    private val navigator: HomeNavigator
) : Fragment() {

    private val viewModel: HomeViewModel by viewModels(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                HomeViewModel(domainRepository, navigator) as T
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        synchronizer.initiateSync()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent { HomeScreen(viewModel) }
    }

}
