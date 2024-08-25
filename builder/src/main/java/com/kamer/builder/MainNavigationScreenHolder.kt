package com.kamer.builder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.util.Consumer
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import easydone.core.domain.DomainRepository
import easydone.core.domain.RemoteDataSource
import easydone.core.domain.SyncScheduler
import easydone.core.domain.model.Task
import easydone.feature.edittask.EditTaskArgs
import easydone.feature.edittask.EditTaskNavigator
import easydone.feature.edittask.EditTaskRoute
import easydone.feature.home.HomeNavigator
import easydone.feature.home.HomeRoute
import easydone.feature.inbox.InboxNavigator
import easydone.feature.inbox.InboxRoute
import easydone.feature.settings.SettingScreen
import easydone.feature.settings.SettingsNavigator
import easydone.feature.setupflow.SetupRoute
import easydone.feature.taskdetails.TaskDetailsNavigator
import easydone.feature.taskdetails.TaskDetailsRoute
import easydone.feature.waiting.WaitingNavigator
import easydone.feature.waiting.WaitingRoute
import easydone.service.trello.TrelloRemoteDataSource
import easydone.service.trello.api.TrelloApi
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainNavigationScreenHolder(
    private val activity: AppCompatActivity
) {
    private val syncScheduler: SyncScheduler by activity.inject()
    private val domainRepository: DomainRepository by activity.inject()
    private val remoteDataSource: RemoteDataSource by activity.inject()
    private val trelloRemoteDataSource: TrelloRemoteDataSource by activity.inject()
    private val trelloApi: TrelloApi by activity.inject()
    private val trelloApiKey: String by activity.inject()

    fun NavController.openViewTask(id: String) {
        navigate("task/$id")
    }

    fun NavController.openEditTask(id: String) {
        navigate("task/edit/$id")
    }

    fun NavController.openCreateTask() {
        navigate("task/create")
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val isRemoteDataSourceConnected = remember {
            runBlocking { remoteDataSource.isConnected() }
        }
        val startDestination = if (isRemoteDataSourceConnected) {
            "home"
        } else {
            "setup"
        }

        LaunchedEffect(Unit) {
            val intent = activity.intent
            val isInboxLaunch = !intent.isOpenedFromHistory() &&
                    intent.getBooleanExtra("inbox", false)
            if (isInboxLaunch) {
                navController.navigate("inbox")
            }
        }

        DisposableEffect(Unit) {
            val listener = Consumer<Intent> { intent ->
                //check inbox deeplink
                if (intent.getBooleanExtra("inbox", false)) {
                    with(navController) {
                        navigate("inbox", navOptions {
                            popUpTo("home") {}
                        })
                    }
                }
            }
            activity.addOnNewIntentListener(listener)
            onDispose { activity.removeOnNewIntentListener(listener) }
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable("setup") {
                SetupRoute(
                    trelloRemoteDataSource = trelloRemoteDataSource,
                    trelloApi = trelloApi,
                    trelloApiKey = trelloApiKey
                ) {
                    navController.popBackStack("setup", true)
                    navController.navigate("home")
                }
            }

            composable("home") {
                HomeRoute(syncScheduler, domainRepository, object : HomeNavigator {
                    override fun navigateToCreate() {
                        navController.openCreateTask()
                    }

                    override fun navigateToSettings() {
                        navController.navigate("settings")
                    }

                    override fun navigateToInbox() {
                        navController.navigate("inbox")
                    }

                    override fun navigateToWaiting() {
                        navController.navigate("waiting")
                    }

                    override fun navigateToTask(id: String) {
                        navController.openViewTask(id)
                    }
                })
            }

            composable("inbox") {
                InboxRoute(
                    domainRepository,
                    object : InboxNavigator {
                        override fun openTask(id: String) {
                            navController.openViewTask(id)
                        }

                        override fun close() {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable("waiting") {
                WaitingRoute(domainRepository, object : WaitingNavigator {
                    override fun openTask(id: String) {
                        navController.openViewTask(id)
                    }
                })
            }

            composable("settings") {
                SettingScreen(remoteDataSource, object : SettingsNavigator {
                    override fun navigateToSetup() {
                        // TODO reset everything
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
                        navController.openEditTask(id)
                    }

                    override fun close() {
                        navController.popBackStack()
                    }
                })
            }

            composable(
                route = "task/edit/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = checkNotNull(backStackEntry.arguments?.getString("taskId")) {
                    "Task id is required"
                }
                val args = remember { EditTaskArgs.Edit(id) }
                EditTaskRoute(args, domainRepository, object : EditTaskNavigator {
                    override fun close() {
                        activity.onBackPressedDispatcher.onBackPressed()
                    }
                })
            }

            composable("task/create") {
                EditTaskRoute(
                    EditTaskArgs.Create(type = Task.Type.ToDo),
                    domainRepository,
                    object : EditTaskNavigator {
                        override fun close() {
                            activity.onBackPressedDispatcher.onBackPressed()
                        }
                    })
            }
        }
    }
}

private fun Intent.isOpenedFromHistory(): Boolean =
    flags == (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)