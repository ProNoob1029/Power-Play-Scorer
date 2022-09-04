package com.phoenix.energizescorer.feature_editor.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phoenix.energizescorer.feature_editor.domain.use_case.MatchUseCases
import com.phoenix.energizescorer.feature_editor.domain.util.Order
import com.phoenix.energizescorer.feature_editor.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val matchUseCases: MatchUseCases
): ViewModel() {

    private val _state = MutableStateFlow(ListState())
    val state = _state.asStateFlow()

    private var job: Job? = null

    init {
        getList()
    }

    private fun getList(newOrder: Order = Order.Date(OrderType.Descending)) {
        job?.cancel()
        job = matchUseCases.getMatches(order = newOrder)
            .onEach { newList ->
                _state.update {
                    it.copy(
                        list = newList,
                        order = newOrder
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}