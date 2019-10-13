package easydone.feature.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.kamer.home.R
import easydone.core.domain.Synchronizer
import easydone.core.utils.logErrors
import easydone.coreui.design.setupToolbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val fragmentFactory: FragmentFactory by inject()
    private val synchronizer: Synchronizer by inject()
    private val navigator: HomeNavigator by inject()

    private var syncView: SyncView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        synchronizer.initiateSync()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        setupToolbar()
        subscribeOnSyncState()
        addTaskView.setOnClickListener { navigator.navigateToCreate() }
        childFragmentManager.commit {
            replace(R.id.container, fragmentFactory.create())
        }
    }

    private fun subscribeOnSyncState() {
        combine(
            synchronizer.isSyncing(),
            synchronizer.observeChanges()
        ) { isSyncing, changesCount -> isSyncing to changesCount }
            .onEach { (isSyncing, changesCount) ->
                syncView?.apply {
                    this.hasChanges = changesCount != 0L
                    this.isSyncing = isSyncing
                }
            }
            .logErrors()
            .launchIn(viewLifecycleOwner.lifecycleScope)
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

    companion object {
        fun create(): Fragment = HomeFragment()
    }

}
