package com.rfcoding.deliverytracker.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    val productName: String,
    val status: OrderStatusDto,
    val createdAt: String
)

enum class OrderStatusDto {
    PENDING,
    IN_TRANSIT,
    COMPLETED
}
