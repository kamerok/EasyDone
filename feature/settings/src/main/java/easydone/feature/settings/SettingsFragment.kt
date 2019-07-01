package easydone.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class SettingsFragment : Fragment() {

    private lateinit var navigator: SettingsNavigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    data class Dependencies(
        val navigator: SettingsNavigator
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SettingsFragment().apply {
            navigator = dependencies.navigator
        }
    }

}