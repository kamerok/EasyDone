package com.kamer.builder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import easydone.feature.edittask.EditTaskFragment
import easydone.feature.quickcreatetask.QuickCreateTaskFragment
import org.koin.core.context.GlobalContext


object CustomFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (className) {
            MainNavigationFragment::class.java.name -> GlobalContext.get()
                .get<MainNavigationFragment>()

            EditTaskFragment::class.java.name -> GlobalContext.get().get<EditTaskFragment>()
            QuickCreateTaskFragment::class.java.name -> GlobalContext.get()
                .get<QuickCreateTaskFragment>()

            else -> super.instantiate(classLoader, className)
        }

}
