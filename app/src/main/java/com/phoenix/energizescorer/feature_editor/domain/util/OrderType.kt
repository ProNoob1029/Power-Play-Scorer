package com.phoenix.energizescorer.feature_editor.domain.util

sealed class OrderType{
    object Ascending: OrderType()
    object Descending: OrderType()
}
