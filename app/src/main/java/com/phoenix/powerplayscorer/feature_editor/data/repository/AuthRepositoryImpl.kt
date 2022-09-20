package com.phoenix.powerplayscorer.feature_editor.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.phoenix.powerplayscorer.feature_editor.domain.model.Response
import com.phoenix.powerplayscorer.feature_editor.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val appContext: Context
): AuthRepository {
    /*private val sharedPref = appContext.getSharedPreferences(
        appContext.getString(R.string.sharedPref),
        Context.MODE_PRIVATE
    )

    private fun isSingedInOffline(): Boolean {
        val value = sharedPref.getBoolean(
            appContext.getString(R.string.isOffline),
            false
        )
        Log.e("idk", if (value) "true" else "false")
        return value
    }*/

    override fun isUserSignedIn(): Boolean = auth.currentUser != null

    override fun loginOnline(email: String, password: String): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            auth.signInWithEmailAndPassword(email, password).await()
            emit(Response.Success(Unit))
        } catch(e: Exception) {
            emit(Response.Failure(e.message))
        }
    }

    override fun register(email: String, password: String): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            auth.createUserWithEmailAndPassword(email, password).await()
            emit(Response.Success(Unit))
        } catch(e: Exception) {
            emit(Response.Failure(e.message))
        }
    }

    override fun getUserId(): String {
        return auth.currentUser?.uid ?: "offline"
    }

    override fun signOut(): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            auth.signOut()
            /*with(sharedPref.edit()) {
                putBoolean(appContext.getString(R.string.isOffline), false)
                commit()
            }*/
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            emit(Response.Failure(e.message))
        }
    }

    override fun signInOffline(): Flow<Response<Unit>> = flow {
        try {
            emit(Response.Loading)
            /*with(sharedPref.edit()) {
                putBoolean(appContext.getString(R.string.isOffline), true)
                apply()
            }*/
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            emit(Response.Failure(e.message))
        }
    }
}