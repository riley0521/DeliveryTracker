package com.rfcoding.deliverytracker.presentation.order_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rfcoding.deliverytracker.domain.Order
import com.rfcoding.deliverytracker.domain.OrderStatus
import com.rfcoding.deliverytracker.presentation.orders.formatted
import com.rfcoding.deliverytracker.presentation.theme.DeliveryTrackerTheme
import com.rfcoding.deliverytracker.presentation.util.format
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDateTime

@Composable
fun OrderDetailScreenRoot(
    id: String,
    onNavigateBack: () -> Unit,
    viewModel: OrderDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OrderDetailScreen(
        id = id,
        state = state,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailScreen(
    id: String,
    state: OrderDetailState,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Order $id")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.order == null || state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = state.order.productName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Status: ${state.order.status.formatted()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Order date: ${state.order.createdAt.format()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DeliveryTrackerTheme {
        OrderDetailScreen(
            id = "1",
            state = OrderDetailState(
                order = Order(
                    id = "1",
                    productName = "Product #1",
                    status = OrderStatus.PENDING,
                    createdAt = LocalDateTime.now()
                )
            ),
            onNavigateBack = {}
        )
    }
}