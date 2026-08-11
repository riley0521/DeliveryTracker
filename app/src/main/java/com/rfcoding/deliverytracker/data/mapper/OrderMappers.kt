package com.rfcoding.deliverytracker.data.mapper

import com.rfcoding.deliverytracker.data.dto.OrderDto
import com.rfcoding.deliverytracker.data.dto.OrderStatusDto
import com.rfcoding.deliverytracker.domain.Order
import com.rfcoding.deliverytracker.domain.OrderStatus
import java.time.Instant
import java.time.ZoneId

fun OrderDto.toDomain(): Order {
    return Order(
        id = id,
        productName = productName,
        status = when (status) {
            OrderStatusDto.PENDING -> OrderStatus.PENDING
            OrderStatusDto.IN_TRANSIT -> OrderStatus.IN_TRANSIT
            OrderStatusDto.COMPLETED -> OrderStatus.COMPLETED
        },
        createdAt = Instant
            .parse(createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    )
}