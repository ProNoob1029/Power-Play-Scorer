package com.phoenix.powerplayscorer

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ScorerApp @Inject constructor(): Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.firestore.clearPersistence()
        Log.e(TAG, "Persistence cleared")
    }
}