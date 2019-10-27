package easydone.library.navigation

import androidx.fragment.app.Fragment


interface Navigator {
    fun openScreen(fragment: Fragment, addToBackStack: Boolean = false)
    fun openScreen(fragmentClass: Class<out Fragment>, addToBackStack: Boolean = false)
    fun isEmpty(): Boolean
    fun popScreen()
    fun clearStack()
}
