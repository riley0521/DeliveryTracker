package com.rfcoding.deliverytracker.presentation.order_detail

import com.rfcoding.deliverytracker.domain.Order

data class OrderDetailState(
    val order: Order? = null,
    val isLoading: Boolean = false
)