package com.kamer.builder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import easydone.feature.home.HomeFragment
import org.koin.core.context.GlobalContext


class CustomFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (className) {
            HomeFragment::class.java.name -> HomeFragment(
                GlobalContext.get().koin.get(),
                GlobalContext.get().koin.get(),
                GlobalContext.get().koin.get()
            )
            else -> super.instantiate(classLoader, className)
        }

}
