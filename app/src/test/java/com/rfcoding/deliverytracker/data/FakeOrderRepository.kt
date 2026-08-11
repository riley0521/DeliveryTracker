package com.rfcoding.deliverytracker.data

import com.rfcoding.deliverytracker.domain.DataError
import com.rfcoding.deliverytracker.domain.Order
import com.rfcoding.deliverytracker.domain.OrderRepository
import com.rfcoding.deliverytracker.domain.OrderStatus
import com.rfcoding.deliverytracker.domain.Result
import kotlinx.coroutines.delay
import java.time.ZonedDateTime

class FakeOrderRepository(
    private val now: ZonedDateTime
): OrderRepository {

    var showError = false
    val dummyOrders = (1..5).map {
        Order(
            id = "$it",
            productName = "Product #$it",
            status = OrderStatus.PENDING,
            createdAt = now.toLocalDateTime()
        )
    }.toMutableList()

    override suspend fun fetchOrders(): Result<List<Order>, DataError.Remote> {
        delay(300L)

        if (showError) {
            return Result.Failure(DataError.Remote.UNKNOWN)
        }
        return Result.Success(dummyOrders)
    }

    override suspend fun getOrderById(id: String): Result<Order, DataError.Remote> {
        if (showError) {
            return Result.Failure(DataError.Remote.UNKNOWN)
        }

        val order = dummyOrders.firstOrNull { it.id == id }
        return if (order == null) {
            Result.Failure(DataError.Remote.NOT_FOUND)
        } else Result.Success(order)
    }
}