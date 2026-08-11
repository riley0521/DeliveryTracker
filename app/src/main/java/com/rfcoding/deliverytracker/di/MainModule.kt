package com.rfcoding.deliverytracker.di

import com.rfcoding.deliverytracker.data.OrderRepositoryImpl
import com.rfcoding.deliverytracker.data.networking.HttpClientFactory
import com.rfcoding.deliverytracker.domain.OrderRepository
import com.rfcoding.deliverytracker.presentation.order_detail.OrderDetailViewModel
import com.rfcoding.deliverytracker.presentation.orders.OrderListViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {
    single<HttpClient> { HttpClientFactory.create() }
    singleOf(::OrderRepositoryImpl).bind<OrderRepository>()
    viewModelOf(::OrderListViewModel)
    viewModelOf(::OrderDetailViewModel)
}