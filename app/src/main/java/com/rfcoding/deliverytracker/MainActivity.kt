package com.rfcoding.deliverytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rfcoding.deliverytracker.presentation.navigation.MainNavigation
import com.rfcoding.deliverytracker.presentation.order_detail.OrderDetailScreenRoot
import com.rfcoding.deliverytracker.presentation.orders.OrderListScreenRoot
import com.rfcoding.deliverytracker.presentation.theme.DeliveryTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeliveryTrackerTheme {
                val navController = rememberNavController()
                NavHost(
                    navController,
                    startDestination = MainNavigation.OrderList
                ) {
                    composable<MainNavigation.OrderList> {
                        OrderListScreenRoot(
                            onOrderClicked = { id ->
                                navController.navigate(MainNavigation.OrderDetail(id))
                            }
                        )
                    }
                    composable<MainNavigation.OrderDetail> {
                        val id = it.toRoute<MainNavigation.OrderDetail>().id
                        OrderDetailScreenRoot(
                            id = id,
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }
}