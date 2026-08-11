package com.rfcoding.deliverytracker.presentation.orders

import com.rfcoding.deliverytracker.domain.Order

data class OrderListState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val filter: OrderFilter = OrderFilter.ALL,
    val query: String = ""
) {
    val sortedOrders: List<Order> get() = orders.sortedByDescending { it.createdAt }
}

enum class OrderFilter {
    ALL,
    PENDING,
    IN_TRANSIT,
    COMPLETED
}