package com.phoenix.powerplayscorer.feature_editor.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.MatchUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val matchUseCases: MatchUseCases
): ViewModel() {
    val state = MutableStateFlow(Match())
    val isEditEnabled = MutableStateFlow(false)

    private var job: Job? = null

    private var currentKey: String? = savedStateHandle.get<String>("key")

    init {
        currentKey?.let {
            getMatch(it)
        }
    }

    private fun getMatch(key: String) {
        job?.cancel()
        job = matchUseCases.getMatch(key)
            .onEach { newMatch ->
                newMatch?.let { match ->
                    state.update { match }
                }
            }
            .launchIn(viewModelScope)
    }
}