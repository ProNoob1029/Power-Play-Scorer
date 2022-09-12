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
                        } * (it.autoFullyParked2 + 1) * it.twoTeams
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
            val text by remember {
                derivedStateOf {
                    if (state.value.twoTeams)
                        "Fully parked 1: "
                    else
                        "Fully parked: "
                }
            }
            val checked by remember { derivedStateOf { state.value.autoFullyParked1 } }
            TextSwitch(
                modifier = modifier,
                text = text,
                checked = checked,
                specialColor = false,
                visible = true,
                enabled = editEnabled.value,
                onChange = { newChecked ->
                    mutableState.update { match ->
                        match.copy(
                            autoFullyParked1 = newChecked
                        )
                    }
                }
            )
        },
        { modifier ->
            val text = "Fully parked 2: "
            val checked by remember { derivedStateOf { state.value.autoFullyParked2 } }
            val visible by remember { derivedStateOf { state.value.twoTeams } }
            TextSwitch(
                modifier = modifier,
                text = text,
                checked = checked,
                specialColor = true,
                visible = visible,
                enabled = editEnabled.value,
                onChange = { newChecked ->
                    mutableState.update { match ->
                        match.copy(
                            autoFullyParked2 = newChecked
                        )
                    }
                }
            )
        },
        { modifier ->
            val counter by remember { derivedStateOf { state.value.autoStorage } }
            TextCounter(
                modifier = modifier,
                text = "Freight in storage: ",
                counter = counter,
                enabled = editEnabled.value,
                onClick = { add ->
                    mutableState.update { match ->
                        if (match.autoStorage + add >= 0) {
                            match.copy(
                                autoStorage = match.autoStorage + add
                            )
                        } else match
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