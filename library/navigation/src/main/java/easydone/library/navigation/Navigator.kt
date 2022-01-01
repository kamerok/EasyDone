package easydone.library.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment


interface Navigator {
    fun openScreen(fragment: Fragment, addToBackStack: Boolean = false)
    fun openScreen(
        fragmentClass: Class<out Fragment>,
        addToBackStack: Boolean = false,
        args: Bundle? = null
    )
    fun setupScreenStack(vararg fragmentClasses: Class<out Fragment>)

    fun isEmpty(): Boolean
    fun popScreen()
    fun clearStack()
}
