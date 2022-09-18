package com.phoenix.powerplayscorer.feature_editor.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.*
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.phoenix.powerplayscorer.feature_editor.presentation.auth.AuthScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.editor.EditorScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.list.ListScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.login.LoginScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.settings.SettingsScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    startDestination: String
) {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(navController = navController, startDestination = startDestination) {
        loginGraph(navController)
        editorGraph(navController)
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.editorGraph(navController: NavController) {
    navigation(startDestination = Screen.ListScreen.route, route = "editor") {
        composable(
            route = Screen.ListScreen.route,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            ListScreen(
                navigate = {
                    navController.navigate(it)
                }
            )
        }
        composable(
            route = Screen.EditorScreen.route + "?key={key}",
            arguments = listOf(
                navArgument(name = "key") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            ),
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            EditorScreen()
        }
        composable(
            route = Screen.SettingsScreen.route,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            SettingsScreen(
                navigateToLogIn = {
                    navController.navigate("login") {
                        popUpTo("editor") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = Screen.ChooseAccount.route, route = "login") {
        composable(
            route = Screen.LoginScreen.route,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            LoginScreen(
                navigateToEditor = {
                    navController.navigate("editor") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.ChooseAccount.route,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            AuthScreen(
                navigate = {
                    navController.navigate(it)
                },
                navigateToEditor = {
                    navController.navigate("editor") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}