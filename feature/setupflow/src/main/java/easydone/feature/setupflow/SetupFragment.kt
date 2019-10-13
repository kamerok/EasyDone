package easydone.feature.setupflow

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kamer.setupflow.R
import easydone.core.network.AuthInfoHolder
import easydone.library.trelloapi.model.Board
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject


class SetupFragment : Fragment(R.layout.fragment_setup) {

    private val navigator: SetupFlowNavigator by inject()
    private val authInfoHolder: AuthInfoHolder by inject()

    private var isLogin = true

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
            lifecycleScope.launch {
                saveData(token, boardId)
                navigator.onFinishSetup()
            }
        }
    }

    private suspend fun saveData(token: String, boardId: String) = withContext(Dispatchers.IO) {
        authInfoHolder.putToken(token)
        authInfoHolder.putBoardId(boardId)
    }

    companion object {
        fun create(): Fragment = SetupFragment()
    }

}
