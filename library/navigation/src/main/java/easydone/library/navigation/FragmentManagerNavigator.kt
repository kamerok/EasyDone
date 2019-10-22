package easydone.library.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit


class FragmentManagerNavigator(private val fragmentManager: FragmentManager, private val containerId: Int) :
    Navigator {

    override fun openScreen(fragment: Fragment, addToBackStack: Boolean) =
        fragmentManager.commit {
            setCustomAnimations(
                R.anim.overlay_open_enter, R.anim.overlay_open_exit,
                R.anim.overlay_close_enter, R.anim.overlay_close_exit
            )
            replace(containerId, fragment)
            if (addToBackStack) addToBackStack(null)
        }

    override fun isEmpty(): Boolean = fragmentManager.backStackEntryCount == 0

    override fun popScreen() {
        fragmentManager.popBackStack()
    }

    override fun clearStack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}
