package com.rfcoding.deliverytracker.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface MainNavigation {

    @Serializable
    data object OrderList: MainNavigation

    @Serializable
    data class OrderDetail(val id: String): MainNavigation
}