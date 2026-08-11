package com.rfcoding.deliverytracker.presentation.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rfcoding.deliverytracker.domain.Order
import com.rfcoding.deliverytracker.domain.OrderStatus
import com.rfcoding.deliverytracker.presentation.theme.DeliveryTrackerTheme
import com.rfcoding.deliverytracker.presentation.util.format
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDateTime

@Composable
fun OrderListScreenRoot(
    onOrderClicked: (String) -> Unit,
    viewModel: OrderListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OrderListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is OrderListAction.OnOrderClicked -> onOrderClicked(action.id)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
private fun OrderListScreen(
    state: OrderListState,
    onAction: (OrderListAction) -> Unit,
) {
    var isFilterExpanded by remember {
        mutableStateOf(false)
    }

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = {
            onAction(OrderListAction.OnRefresh)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = {
                            onAction(OrderListAction.OnSearchQueryChanged(it))
                        },
                        placeholder = {
                            Text(
                                text = "Search order by product",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black.copy(0.5f)
                            )
                        },
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Column {
                        IconButton(
                            onClick = {
                                isFilterExpanded = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = isFilterExpanded,
                            onDismissRequest = {
                                isFilterExpanded = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    FilterItem(
                                        title = "All",
                                        isSelected = state.filter == OrderFilter.ALL
                                    )
                                },
                                onClick = {
                                    onAction(OrderListAction.OnFilterChanged(OrderFilter.ALL))
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    FilterItem(
                                        title = "Pending",
                                        isSelected = state.filter == OrderFilter.PENDING
                                    )
                                },
                                onClick = {
                                    onAction(OrderListAction.OnFilterChanged(OrderFilter.PENDING))
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    FilterItem(
                                        title = "On the way",
                                        isSelected = state.filter == OrderFilter.IN_TRANSIT
                                    )
                                },
                                onClick = {
                                    onAction(OrderListAction.OnFilterChanged(OrderFilter.IN_TRANSIT))
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    FilterItem(
                                        title = "Completed",
                                        isSelected = state.filter == OrderFilter.COMPLETED
                                    )
                                },
                                onClick = {
                                    onAction(OrderListAction.OnFilterChanged(OrderFilter.COMPLETED))
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(state.sortedOrders) { order ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        onAction(OrderListAction.OnOrderClicked(order.id))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = order.productName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Status: ${order.status.formatted()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Order date: ${order.createdAt.format()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FilterItem(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title
        )
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
        }
    }
}

fun OrderStatus.formatted(): String {
    return when (this) {
        OrderStatus.PENDING -> "Pending"
        OrderStatus.IN_TRANSIT -> "On the way"
        OrderStatus.COMPLETED -> "Completed"
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    DeliveryTrackerTheme {
        val dummyOrders = (1..5).map {
            Order(
                id = "id$it",
                productName = "Product #$it",
                status = OrderStatus.PENDING,
                createdAt = LocalDateTime.now()
            )
        }

        OrderListScreen(
            state = OrderListState(
                orders = dummyOrders
            ),
            onAction = {}
        )
    }
}