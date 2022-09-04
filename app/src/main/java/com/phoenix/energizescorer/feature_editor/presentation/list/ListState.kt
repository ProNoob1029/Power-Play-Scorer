package com.phoenix.energizescorer.feature_editor.presentation.list

import com.phoenix.energizescorer.feature_editor.domain.model.Match
import com.phoenix.energizescorer.feature_editor.domain.util.Order
import com.phoenix.energizescorer.feature_editor.domain.util.OrderType

data class ListState(
    val list: List<Match> = emptyList(),
    val order: Order = Order.Date(OrderType.Descending)
)
