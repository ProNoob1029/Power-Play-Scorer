package com.phoenix.powerplayscorer.feature_editor.presentation.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val passVisible: Boolean = false
)
