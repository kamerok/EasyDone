package easydone.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import easydone.core.network.AuthInfoHolder


class SettingsFragment(
    private val authInfoHolder: AuthInfoHolder,
    private val navigator: SettingsNavigator
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            SettingScreen(authInfoHolder, navigator)
        }
    }

}
