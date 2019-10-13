package easydone.feature.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import easydone.core.network.AuthInfoHolder
import easydone.coreui.design.setupToolbar
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val authInfoHolder: AuthInfoHolder by inject()
    private val navigator: SettingsNavigator by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.title)
        logoutView.setOnClickListener {
            authInfoHolder.clear()
            navigator.navigateToSetup()
        }
    }

    companion object {
        fun create(): Fragment = SettingsFragment()
    }

}
