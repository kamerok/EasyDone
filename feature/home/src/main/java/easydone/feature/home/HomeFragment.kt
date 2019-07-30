package easydone.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.kamer.home.R
import easydone.core.domain.DomainRepository
import easydone.core.domain.Synchronizer
import easydone.core.utils.logErrors
import easydone.core.utils.onEachMain
import easydone.coreui.design.setupToolbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.launchIn


class HomeFragment : Fragment() {

    private lateinit var fragmentFactory: () -> Fragment
    private lateinit var repository: DomainRepository
    private lateinit var synchronizer: Synchronizer
    private lateinit var navigator: HomeNavigator

    private var syncView: SyncView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        synchronizer.initiateSync()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        subscribeOnSyncState()
        addTaskView.setOnClickListener { navigator.navigateToCreate() }
        childFragmentManager.commit {
            replace(R.id.container, fragmentFactory())
        }
    }

    private fun subscribeOnSyncState() {
        synchronizer.isSyncing()
            .combineLatest(synchronizer.observeChanges()) { isSyncing, changesCount ->
                isSyncing to changesCount
            }
            .onEachMain { (isSyncing, changesCount) ->
                syncView?.apply {
                    this.hasChanges = changesCount != 0L
                    this.isSyncing = isSyncing
                }
            }
            .logErrors()
            .launchIn(GlobalScope)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.home_toolbar, menu)

    override fun onPrepareOptionsMenu(menu: Menu) {
        val menuItem = menu.findItem(R.id.action_sync)
        val syncView = SyncView(requireContext())
        syncView.listener = { onOptionsItemSelected(menuItem) }
        menuItem.actionView = syncView
        this.syncView = syncView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> {
            navigator.navigateToSettings()
            true
        }
        R.id.action_sync -> {
            synchronizer.initiateSync()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    data class Dependencies(
        val fragmentFactory: () -> Fragment,
        val repository: DomainRepository,
        val synchronizer: Synchronizer,
        val navigator: HomeNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = HomeFragment().apply {
            fragmentFactory = dependencies.fragmentFactory
            repository = dependencies.repository
            synchronizer = dependencies.synchronizer
            navigator = dependencies.navigator
        }
    }

}
