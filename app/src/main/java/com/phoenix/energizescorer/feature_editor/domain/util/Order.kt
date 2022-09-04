package com.phoenix.energizescorer.feature_editor.domain.util

sealed class Order(val orderType: OrderType) {
    class Name(orderType: OrderType): Order(orderType)
    class Date(orderType: OrderType): Order(orderType)
    class Points(orderType: OrderType): Order(orderType)
}
