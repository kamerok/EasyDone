package easydone.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import easydone.core.domain.SyncScheduler


class HomeFragment(
    private val syncScheduler: SyncScheduler,
    private val domainRepository: DomainRepository,
    private val navigator: HomeNavigator
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent { HomeRoute(syncScheduler, domainRepository, navigator) }
    }

}
