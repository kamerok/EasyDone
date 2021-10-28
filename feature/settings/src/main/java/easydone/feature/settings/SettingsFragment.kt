package easydone.feature.settings

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import easydone.core.network.AuthInfoHolder
import easydone.coreui.design.setupToolbar
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.launch


class SettingsFragment(
    private val authInfoHolder: AuthInfoHolder,
    private val navigator: SettingsNavigator
) : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.title)
        logoutView.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                authInfoHolder.clear()
                navigator.navigateToSetup()
            }
        }

        view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            insets.consumeSystemWindowInsets()
        }
    }

}
