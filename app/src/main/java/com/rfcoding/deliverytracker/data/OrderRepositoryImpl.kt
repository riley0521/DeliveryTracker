package com.rfcoding.deliverytracker.data

import com.rfcoding.deliverytracker.data.dto.OrderDto
import com.rfcoding.deliverytracker.data.mapper.toDomain
import com.rfcoding.deliverytracker.data.networking.get
import com.rfcoding.deliverytracker.domain.DataError
import com.rfcoding.deliverytracker.domain.Order
import com.rfcoding.deliverytracker.domain.OrderRepository
import com.rfcoding.deliverytracker.domain.Result
import com.rfcoding.deliverytracker.domain.map
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val client: HttpClient
): OrderRepository {

    override suspend fun fetchOrders(): Result<List<Order>, DataError.Remote> {
        return withContext(Dispatchers.IO) {
            client.get<List<OrderDto>>(route = "/orders").map { orders ->
                orders.map { it.toDomain() }
            }
        }
    }

    override suspend fun getOrderById(id: String): Result<Order, DataError.Remote> {
        return withContext(Dispatchers.IO) {
            client.get<OrderDto>(route = "/orders/$id").map {
                it.toDomain()
            }
        }
    }
}