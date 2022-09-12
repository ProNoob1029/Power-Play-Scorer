package com.phoenix.powerplayscorer.feature_editor.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.phoenix.powerplayscorer.feature_editor.presentation.editor.EditorScreen
import com.phoenix.powerplayscorer.feature_editor.presentation.list.ListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ListScreen.route) {
        composable(route = Screen.ListScreen.route) {
            ListScreen(navController = navController)
        }
        composable(
            route = Screen.EditorScreen.route + "?key={key}",
            arguments = listOf(
                navArgument(name = "key") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) {
            EditorScreen()
        }
    }
}