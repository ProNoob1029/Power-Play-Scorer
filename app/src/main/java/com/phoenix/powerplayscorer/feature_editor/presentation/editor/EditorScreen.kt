package com.phoenix.powerplayscorer.feature_editor.presentation.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.presentation.editor.components.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    //navController: NavController = rememberNavController(),
    viewModel: EditorViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val editEnabled = viewModel.isEditEnabled.collectAsState()
    val screenList = remember {
        screenList(
            state = state,
            mutableState = viewModel.state,
            editEnabled = editEnabled
        )
    }
    val twoTeams by remember { derivedStateOf { state.value.twoTeams } }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.isEditEnabled.update { !it }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Edit, "Edit")
            }
        },
        topBar = {
            TopAppBar(
                checked = twoTeams,
                onCheckedChange = { newChecked ->
                    viewModel.state.update { match ->
                        match.copy(
                            twoTeams = newChecked
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
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
    mutableState: MutableStateFlow<Match>,
    editEnabled: State<Boolean>
): List<@Composable (modifier: Modifier) -> Unit> {
    val autoUpperLimit by derivedStateOf {
        12 - state.value.let {
            it.autoTerminal + it.autoGroundJunction + it.autoLowJunction +
                    it.autoMediumJunction + it.autoHighJunction
        }
    }
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
                enabled = editEnabled.value
            )
        },
        { modifier ->
            val activeIndex by remember { derivedStateOf { state.value.alliance } }
            AllianceButtons(
                modifier = modifier,
                firstText = "Red Alliance",
                secondText = "Blue Alliance",
                activeIndex = activeIndex,
                onButtonClicked = { index ->
                    mutableState.update { match ->
                        match.copy(
                            alliance = if (match.alliance == index) null else index
                        )
                    }
                },
                enabled = editEnabled.value
            )
        },
        { modifier ->
            val autoPoints by remember {
                derivedStateOf {
                    state.value.let {
                        it.autoTerminal +
                        it.autoGroundJunction * 2 +
                        it.autoLowJunction * 3 +
                        it.autoMediumJunction * 4 +
                        it.autoHighJunction * 5 +
                        when (it.autoParked1) {
                            false -> 2
                            true -> 10 * it.customSignalSleeve1
                            else -> 0
                        } +
                        when (it.autoParked2) {
                            false -> 2
                            true -> 10 * it.customSignalSleeve2
                            else -> 0
                        }
                    }
                }
            }
            Title(
                modifier = modifier,
                title = "Autonomous points: ",
                counter = autoPoints
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoTerminal } }
            //val upperLimit by remember { derivedStateOf {  } }
            TextCounter(
                modifier = modifier,
                text = "Cones in Terminal: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoTerminal = match.autoTerminal + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoGroundJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones in Ground Junction: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoGroundJunction = match.autoGroundJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoLowJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones in Low Junction: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoLowJunction = match.autoLowJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoMediumJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones in Medium Junction: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoMediumJunction = match.autoMediumJunction + add
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoHighJunction } }
            TextCounter(
                modifier = modifier,
                text = "Cones in High Junction: ",
                counter = counter,
                enabled = editEnabled.value,
                lowerLimit = 0,
                upperLimit = autoUpperLimit + counter,
                onClick = { add ->
                    mutableState.update { match ->
                        match.copy(
                            autoHighJunction = match.autoHighJunction + add
                        )
                    }
                }
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