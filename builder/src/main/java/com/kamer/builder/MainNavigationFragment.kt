package com.kamer.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import easydone.core.domain.DomainRepository
import easydone.core.domain.SyncScheduler
import easydone.feature.home.HomeNavigator
import easydone.feature.home.HomeRoute
import easydone.feature.taskdetails.TaskDetailsNavigator
import easydone.feature.taskdetails.TaskDetailsRoute
import easydone.feature.waiting.WaitingNavigator
import easydone.feature.waiting.WaitingRoute

class MainNavigationFragment(
    private val syncScheduler: SyncScheduler,
    private val domainRepository: DomainRepository,
    private val homeNavigator: HomeNavigator,
    private val taskDetailsNavigator: TaskDetailsNavigator,
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {

        fun NavController.openViewTask(id: String) {
            navigate("task/$id")
        }

        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeRoute(syncScheduler, domainRepository, object : HomeNavigator {
                        override fun navigateToCreate() = homeNavigator.navigateToCreate()

                        override fun navigateToSettings() = homeNavigator.navigateToSettings()

                        override fun navigateToInbox() = homeNavigator.navigateToInbox()

                        override fun navigateToWaiting() {
                            navController.navigate("waiting")
                        }

                        override fun navigateToTask(id: String) {
                            navController.openViewTask(id)
                        }
                    })
                }

                composable("waiting") {
                    WaitingRoute(domainRepository, object : WaitingNavigator {
                        override fun openTask(id: String) {
                            navController.openViewTask(id)
                        }
                    })
                }

                composable(
                    route = "task/{taskId}",
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = checkNotNull(backStackEntry.arguments?.getString("taskId")) {
                        "Task id is required"
                    }
                    TaskDetailsRoute(id, domainRepository, object : TaskDetailsNavigator {
                        override fun editTask(id: String) {
                            taskDetailsNavigator.editTask(id)
                        }

                        override fun close() {
                            navController.popBackStack()
                        }
                    })
                }
            }
        }
    }

}