package com.phoenix.energizescorer.feature_editor.presentation

sealed class Screen(val route: String) {
    object ListScreen: Screen("list_screen")
    object EditorScreen: Screen("editor_screen")

    fun withArgs(vararg args: String): String {
        return  buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
