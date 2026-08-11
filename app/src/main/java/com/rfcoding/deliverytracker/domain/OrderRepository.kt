package com.rfcoding.deliverytracker.domain

interface OrderRepository {

    suspend fun fetchOrders(): Result<List<Order>, DataError.Remote>

    suspend fun getOrderById(id: String): Result<Order, DataError.Remote>
}