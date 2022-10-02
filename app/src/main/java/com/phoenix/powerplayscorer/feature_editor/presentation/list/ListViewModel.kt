package com.phoenix.powerplayscorer.feature_editor.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.powerplayscorer.feature_editor.domain.model.Match
import com.phoenix.powerplayscorer.feature_editor.domain.use_case.database.MatchUseCases
import com.phoenix.powerplayscorer.feature_editor.domain.util.Order
import com.phoenix.powerplayscorer.feature_editor.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val matchUseCases: MatchUseCases
): ViewModel() {

    private val _state = MutableStateFlow(ListState())
    val state = _state.asStateFlow()

    private var job: Job? = null
    private var recentlyDeletedMatches: List<Match>? = null

    init {
        getList()
    }

    fun selectItem(id: String) {
        _state.update {
            it.copy(
                selectedItems = if (it.selectedItems.contains(id))
                    it.selectedItems - id
                else it.selectedItems + id
            )
        }
    }

    fun clearSelectedItems() {
        _state.update {
            it.copy(
                selectedItems = emptyList()
            )
        }
    }

    fun deleteSelectedMatches(
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            state.value.let { listState ->
                val deletedMatches = listState.list.filter {
                    listState.selectedItems.contains(it.key)
                }
                recentlyDeletedMatches = deletedMatches
                matchUseCases.deleteMatches(deletedMatches)
            }
            _state.update { it.copy(selectedItems = emptyList()) }
            onSuccess()
        }
    }

    fun restoreMatches() {
        viewModelScope.launch {
            recentlyDeletedMatches?.let {
                matchUseCases.saveMatches(
                    it.map { match ->
                        match.copy(
                            uploadStamp = null
                        )
                    }
                )
            }
        }
    }

    private fun getList(newOrder: Order = Order.Date(OrderType.Descending)) {
        job?.cancel()
        job = viewModelScope.launch {
            matchUseCases.getMatches(order = newOrder).collectLatest {newList ->
                _state.update {
                    it.copy(
                        list = newList,
                        order = newOrder
                    )
                }
            }
        }
    }
}