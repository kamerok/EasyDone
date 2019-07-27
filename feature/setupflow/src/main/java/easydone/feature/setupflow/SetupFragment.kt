package easydone.feature.setupflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.kamer.setupflow.R
import easydone.core.network.AuthInfoHolder
import easydone.library.trelloapi.model.Board
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SetupFragment : Fragment() {

    private lateinit var finishListener: () -> Unit
    private lateinit var navigator: SetupFlowNavigator
    private lateinit var authInfoHolder: AuthInfoHolder

    private var isLogin = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_setup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLogin()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!isLogin) {
                        startLogin()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                        isEnabled = true
                    }
                }
            })
    }

    private fun startLogin() {
        isLogin = true
        navigator.navigateToLogin { token, boards ->
            startSelectBoard(token, boards)
        }
    }

    private fun startSelectBoard(token: String, boards: List<Board>) {
        isLogin = false
        navigator.navigateToSelectBoard(boards) { boardId ->
            GlobalScope.launch(Dispatchers.Main) {
                saveData(token, boardId)
                finishListener()
            }
        }
    }

    private suspend fun saveData(token: String, boardId: String) = withContext(Dispatchers.IO) {
        authInfoHolder.putToken(token)
        authInfoHolder.putBoardId(boardId)
    }

    data class Dependencies(
        val finishSetupListener: () -> Unit,
        val navigator: SetupFlowNavigator,
        val authInfoHolder: AuthInfoHolder
    )

    companion object {
        fun create(dependencies: Dependencies): Fragment = SetupFragment().apply {
            finishListener = dependencies.finishSetupListener
            navigator = dependencies.navigator
            authInfoHolder = dependencies.authInfoHolder
        }
    }

}