package easydone.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import easydone.core.auth.AuthInfoHolder
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    private lateinit var authInfoHolder: AuthInfoHolder
    private lateinit var navigator: SettingsNavigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logoutView.setOnClickListener {
            authInfoHolder.clear()
            navigator.navigateToSetup()
        }
    }

    data class Dependencies(
        val authInfoHolder: AuthInfoHolder,
        val navigator: SettingsNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SettingsFragment().apply {
            authInfoHolder = dependencies.authInfoHolder
            navigator = dependencies.navigator
        }
    }

}