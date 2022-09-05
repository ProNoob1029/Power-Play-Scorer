package com.phoenix.energizescorer.feature_editor.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.energizescorer.feature_editor.domain.model.Match
import com.phoenix.energizescorer.feature_editor.domain.use_case.MatchUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val matchUseCases: MatchUseCases
): ViewModel() {
    private val _state = MutableStateFlow(Match())
    val state = _state.asStateFlow()

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
                    _state.update { match }
                }
            }
            .launchIn(viewModelScope)
    }
}