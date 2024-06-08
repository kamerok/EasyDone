package easydone.feature.setupflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import easydone.service.trello.TrelloRemoteDataSource
import easydone.service.trello.api.TrelloApi


class SetupFragment(
    private val trelloRemoteDataSource: TrelloRemoteDataSource,
    private val trelloApi: TrelloApi,
    private val trelloApiKey: String,
    private val onFinishSetup: () -> Unit
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            SetupRoute(
                trelloRemoteDataSource = trelloRemoteDataSource,
                trelloApi = trelloApi,
                trelloApiKey = trelloApiKey,
                onFinishSetup = onFinishSetup
            )
        }
    }

}
