package com.rfcoding.deliverytracker.domain

import java.time.LocalDateTime

data class Order(
    val id: String,
    val productName: String,
    val status: OrderStatus,
    val createdAt: LocalDateTime
)

enum class OrderStatus {
    PENDING,
    IN_TRANSIT,
    COMPLETED
}