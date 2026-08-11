package com.rfcoding.deliverytracker.presentation.orders

sealed interface OrderListAction {
    data class OnOrderClicked(val id: String): OrderListAction
    data class OnFilterChanged(val filter: OrderFilter): OrderListAction
    data class OnSearchQueryChanged(val text: String): OrderListAction
    data object OnRefresh: OrderListAction
}