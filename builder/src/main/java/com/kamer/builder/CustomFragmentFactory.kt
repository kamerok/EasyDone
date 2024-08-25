package com.kamer.builder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import org.koin.core.context.GlobalContext


object CustomFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (className) {
            MainNavigationFragment::class.java.name -> GlobalContext.get()
                .get<MainNavigationFragment>()

            else -> super.instantiate(classLoader, className)
        }

}
