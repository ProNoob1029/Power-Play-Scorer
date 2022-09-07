package com.phoenix.energizescorer.feature_editor.presentation.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.phoenix.energizescorer.feature_editor.domain.model.Match
import com.phoenix.energizescorer.feature_editor.presentation.editor.components.AllianceButtons
import com.phoenix.energizescorer.feature_editor.presentation.editor.components.TextField
import com.phoenix.energizescorer.feature_editor.presentation.editor.components.Title
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
        { modifier ->
            val text by remember { derivedStateOf { state.value.title } }
            TextField(
                modifier = modifier,
                label = "Title",
                text = text,
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
        { modifier ->
            val activeIndex by remember { derivedStateOf { state.value.alliance } }
            AllianceButtons(
                modifier = modifier,
                firstText = "Red Alliance",
                secondText = "Blue Alliance",
                activeIndex = activeIndex,
                onButtonClicked =
                { index ->
                    mutableState.update { match ->
                        match.copy(
                            alliance = if (match.alliance == index) null else index
                        )
                    }
                },
                enabled = true
            )
        },
        { modifier ->
            val autoPoints by remember {
                derivedStateOf {
                    state.value.let {
                        it.autoDuck * 10 +
                                it.autoFreightBonus1 * 10 +
                                it.autoFreightBonus2 * 10 * it.twoTeams +
                                it.autoStorage * 2 +
                                (it.autoHub1 + it.autoHub2 + it.autoHub3) * 6 +
                                when(it.autoParked1) {
                                    false -> 3
                                    true -> 5
                                    else -> 0
                                } * (it.autoFullyParked1 + 1) +
                                when(it.autoParked2) {
                                    false -> 3
                                    true -> 5
                                    else -> 0
                                } * (it.autoFullyParked2 + 1) * it.twoTeams +
                                it.alliance * 2
                    }
                }
            }
            Title(
                modifier = modifier,
                title = "Autonomous points: ",
                counter = autoPoints
            )
        }
    )
}

private operator fun Boolean.plus(other: Int) = if (this) other + 1 else other
private operator fun Boolean.times(other: Int) = if (this) other else 0
private operator fun Int.times(other: Boolean) = if (other) this else 0

private operator fun Boolean?.plus(other: Int) = if (this == null) 0 else this + 1 + other
private operator fun Boolean?.times(other: Int) = if (this == null) 0 else (this + 1) * other
private operator fun Int.times(other: Boolean?) = if (other == null) 0 else (other + 1) * this