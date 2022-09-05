package com.phoenix.energizescorer.feature_editor.presentation.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.phoenix.energizescorer.feature_editor.domain.model.Match
import com.phoenix.energizescorer.feature_editor.presentation.editor.components.TextField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavController = rememberNavController(),
    viewModel: EditorViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val screenList = remember {
        screenList(
            state = state,
            mutableState = viewModel.state
        )
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(
                items = screenList
            ) { item ->
                item(Modifier)
            }
        }
    }
}

fun screenList(
    state: State<Match>,
    mutableState: MutableStateFlow<Match>
): List<@Composable (modifier: Modifier) -> Unit> {
    return listOf(
        {
            TextField(
                modifier = it,
                label = "Title",
                text = state.value.title,
                onValueChange = { newString ->
                    mutableState.update {
                        it.copy(
                            title = newString
                        )
                    }
                },
                enabled = true
            )
        },
        {
            Text(text = state.value.title)
        },
        {
            Text(text = state.value.autoHub1.toString())
        },
        {
            Button(
                onClick = {
                    mutableState.update { match ->
                        match.copy(
                            autoHub1 = match.autoHub1 + 1
                        )
                    }
                }
            ) {
                Text(text = "wow")
            }
        }
    )
}