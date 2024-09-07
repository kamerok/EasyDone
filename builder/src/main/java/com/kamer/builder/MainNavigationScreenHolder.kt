package com.kamer.builder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import easydone.core.domain.LocalDataSource
import easydone.core.domain.RemoteDataSource
import easydone.core.domain.SyncScheduler
import easydone.core.domain.model.Task
import easydone.coreui.design.LocalNavAnimatedVisibilityScope
import easydone.coreui.design.LocalSharedTransitionScope
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


private enum class ScaleTransitionDirection {
    INWARDS, OUTWARDS
}

private fun scaleIntoContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
    initialScale: Float = if (direction == ScaleTransitionDirection.OUTWARDS) 0.9f else 1.1f
): EnterTransition = scaleIn(
    animationSpec = tween(220, delayMillis = 90),
    initialScale = initialScale
) + fadeIn(animationSpec = tween(220, delayMillis = 90))

private fun scaleOutOfContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
    targetScale: Float = if (direction == ScaleTransitionDirection.INWARDS) 0.9f else 1.1f
): ExitTransition = scaleOut(
    animationSpec = tween(
        durationMillis = 220,
        delayMillis = 90
    ), targetScale = targetScale
) + fadeOut(tween(delayMillis = 90))

private fun taskExitMove(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(
            220,
            delayMillis = 90
        ),
    ) { it } + fadeOut(tween(delayMillis = 90))

private fun taskExitArchive(): ExitTransition =
    slideOutVertically(
        animationSpec = tween(
            220,
            delayMillis = 90
        ),
    ) { it } + fadeOut(tween(delayMillis = 90))

private enum class TaskPopAnimation {
    Move, Archive
}

private const val TASK_POP_ANIMATION_KEY = "task_close_animation"


class MainNavigationScreenHolder(
    private val activity: AppCompatActivity
) {
    private val syncScheduler: SyncScheduler by activity.inject()
    private val domainRepository: DomainRepository by activity.inject()
    private val remoteDataSource: RemoteDataSource by activity.inject()
    private val localDataSource: LocalDataSource by activity.inject()
    private val trelloRemoteDataSource: TrelloRemoteDataSource by activity.inject()
    private val trelloApi: TrelloApi by activity.inject()
    private val trelloApiKey: String by lazy { trelloRemoteDataSource.apiKey }

    fun NavController.openViewTask(id: String) {
        navigate("task/$id")
    }

    fun NavController.openEditTask(id: String) {
        navigate("task/edit/$id")
    }

    fun NavController.openCreateTask() {
        navigate("task/create")
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun MainScreen() {
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this
            ) {
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

                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = { scaleIntoContainer() },
                    exitTransition = { scaleOutOfContainer(direction = ScaleTransitionDirection.INWARDS) },
                    popEnterTransition = { scaleIntoContainer(direction = ScaleTransitionDirection.OUTWARDS) },
                    popExitTransition = { scaleOutOfContainer() }
                ) {
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
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            HomeRoute(
                                syncScheduler = syncScheduler,
                                domainRepository = domainRepository,
                                navigator = object : HomeNavigator {
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
                                }
                            )
                        }
                    }

                    composable("inbox") {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
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
                    }

                    composable("waiting") {
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            WaitingRoute(domainRepository, object : WaitingNavigator {
                                override fun openTask(id: String) {
                                    navController.openViewTask(id)
                                }
                            })
                        }
                    }

                    composable("settings") {
                        SettingScreen(
                            remoteDataSource,
                            localDataSource,
                            object : SettingsNavigator {
                                override fun navigateToSetup() {
                                    navController.navigate("setup", navOptions {
                                        popUpTo("home") { inclusive = true }
                                    })
                                }
                            })
                    }

                    composable(
                        route = "task/{taskId}",
                        arguments = listOf(navArgument("taskId") { type = NavType.StringType }),
                        popExitTransition = {
                            val animation = initialState.savedStateHandle
                                .get<TaskPopAnimation>(TASK_POP_ANIMATION_KEY)
                            when (animation) {
                                TaskPopAnimation.Move -> taskExitMove()
                                TaskPopAnimation.Archive -> taskExitArchive()
                                else -> scaleOutOfContainer()
                            }
                        }
                    ) { backStackEntry ->
                        CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                            val id = checkNotNull(backStackEntry.arguments?.getString("taskId")) {
                                "Task id is required"
                            }
                            TaskDetailsRoute(id, domainRepository, object : TaskDetailsNavigator {
                                override fun editTask(id: String) {
                                    navController.openEditTask(id)
                                }

                                override fun closeMove() {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(TASK_POP_ANIMATION_KEY, TaskPopAnimation.Move)
                                    navController.popBackStack()
                                }

                                override fun closeArchive() {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set(TASK_POP_ANIMATION_KEY, TaskPopAnimation.Archive)
                                    navController.popBackStack()
                                }
                            })
                        }
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
    }
}

private fun Intent.isOpenedFromHistory(): Boolean =
    flags == (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY)