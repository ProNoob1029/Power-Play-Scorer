package com.phoenix.energizescorer.feature_editor.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.phoenix.energizescorer.ui.theme.EnergizeScorerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnergizeScorerTheme {
                Navigation()
            }
        }
    }
}