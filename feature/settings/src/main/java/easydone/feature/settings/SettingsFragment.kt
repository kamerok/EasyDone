package easydone.feature.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import easydone.core.network.AuthInfoHolder
import easydone.coreui.design.setupToolbar
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment(
    private val authInfoHolder: AuthInfoHolder,
    private val navigator: SettingsNavigator
) : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.title)
        logoutView.setOnClickListener {
            authInfoHolder.clear()
            navigator.navigateToSetup()
        }
    }

}
