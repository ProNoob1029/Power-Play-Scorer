package com.phoenix.powerplayscorer.feature_editor.presentation.auth

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(): ViewModel() {
    val state = MutableStateFlow(AuthState())

    fun register(
        onSuccess: () -> Unit,
        onFailure: (java.lang.Exception?) -> Unit
    ) {
        state.value.let { authState ->
            Firebase.auth.createUserWithEmailAndPassword(authState.email, authState.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        onSuccess()
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        onFailure(task.exception)
                    }
                }
        }
    }
}