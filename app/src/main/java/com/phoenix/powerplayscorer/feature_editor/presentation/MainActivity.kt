package com.phoenix.powerplayscorer.feature_editor.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.phoenix.powerplayscorer.ui.theme.PowerPlayScorerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PowerPlayScorerTheme(
                dynamicColor = true
            ) {
                val currentUser by remember { derivedStateOf { Firebase.auth.currentUser } }
                Navigation(currentUser)
            }
        }
    }
}