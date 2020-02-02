package easydone.feature.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.marginBottom
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
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


class HomeFragment(
    private val contentFragmentClass: Class<out Fragment>,
    private val synchronizer: Synchronizer,
    private val navigator: HomeNavigator
) : Fragment(R.layout.fragment_home) {

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
            replace(R.id.container, contentFragmentClass, null)
        }

        view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets.replaceSystemWindowInsets(
                insets.systemWindowInsetLeft,
                0,
                insets.stableInsetRight,
                insets.systemWindowInsetBottom
            )
        }
        addTaskView.setOnApplyWindowInsetsListener(object : View.OnApplyWindowInsetsListener {
            private var originalMargin = 0

            override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
                if (originalMargin == 0) {
                    originalMargin = v.marginBottom
                }
                (v.layoutParams as? ViewGroup.MarginLayoutParams)
                    ?.updateMargins(bottom = originalMargin + insets.systemWindowInsetBottom)
                return insets
            }
        })
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

}
