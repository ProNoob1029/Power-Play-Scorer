package com.phoenix.powerplayscorer.feature_editor.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.MatchUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val matchUseCases: MatchUseCases
): ViewModel() {
    private var currentKey: String? = savedStateHandle.get<String>("key")

    val state = MutableStateFlow(Match())
    val isEditEnabled = MutableStateFlow(currentKey == null)
    val isNewMatch = MutableStateFlow(currentKey == null)

    private var job: Job? = null

    init {
        getMatch(currentKey)
    }

    fun save() {
        isEditEnabled.update { false }
        viewModelScope.launch {
            state.value.let { match ->
                matchUseCases.saveMatch(
                    match.copy(
                        createStamp = if (currentKey == null)
                            System.currentTimeMillis()
                        else match.createStamp,
                        editStamp = System.currentTimeMillis(),
                        totalPoints = calculateTotalPoints(match)
                    )
                )
                currentKey = match.key
                getMatch(currentKey)
            }
        }
    }

    fun edit() {
        isEditEnabled.update { true }
        job?.cancel()
    }

    fun reset() {
        state.update { Match() }
    }

    private fun getMatch(key: String?) {
        key?.let {
            job?.cancel()
            job = matchUseCases.getMatch(it)
                .onEach { newMatch ->
                    newMatch?.let { match ->
                        state.update { match }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun calculateTotalPoints(it: Match): Int {
        val autoPoints = it.autoTerminal +
                it.autoGroundJunction * 2 +
                it.autoLowJunction * 3 +
                it.autoMediumJunction * 4 +
                it.autoHighJunction * 5 +
                when (it.autoParked1) {
                    false -> 2
                    true -> 10 * (it.customSignalSleeve1 + 1)
                    else -> 0
                } +
                when (it.autoParked2) {
                    false -> 2
                    true -> 10 * (it.customSignalSleeve2 + 1)
                    else -> 0
                } * it.twoTeams
        val driverPoints = it.driverTerminal +
                it.driverGroundJunction * 2 +
                it.driverLowJunction * 3 +
                it.driverMediumJunction * 4 +
                it.driverHighJunction * 5
        val endgamePoints = it.junctionsOwnedByCone * 3 +
                it.junctionsOwnedByBeacons * 10 +
                it.circuitCompleted * 20 +
                it.endParked1 * 2 +
                it.endParked2 * 2 * it.twoTeams
        return autoPoints + driverPoints + endgamePoints
    }
}