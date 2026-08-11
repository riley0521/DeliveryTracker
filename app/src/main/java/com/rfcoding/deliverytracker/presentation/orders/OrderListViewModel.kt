package com.rfcoding.deliverytracker.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfcoding.deliverytracker.domain.Order
import com.rfcoding.deliverytracker.domain.OrderRepository
import com.rfcoding.deliverytracker.domain.OrderStatus
import com.rfcoding.deliverytracker.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderListViewModel(
    private val orderRepository: OrderRepository
): ViewModel() {

    private var hasLoadedInitialData = false

    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())

    private val _state = MutableStateFlow(OrderListState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadOrders()
                observeQuery()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OrderListState()
        )

    private fun observeQuery() {
        _state
            .debounce(200L)
            .map { it.query }
            .distinctUntilChanged()
            .onEach { query ->
                updateItemsByQuery(query)
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: OrderListAction) {
        when (action) {
            is OrderListAction.OnFilterChanged -> {
                _state.update { it.copy(filter = action.filter, query = "") }
                updateItemsByFilter(action.filter)
            }
            is OrderListAction.OnSearchQueryChanged -> {
                _state.update { it.copy(query = action.text.trim(), filter = OrderFilter.ALL) }
            }
            OrderListAction.OnRefresh -> loadOrders()
            else -> Unit
        }
    }

    private fun loadOrders() = viewModelScope.launch {
        if (_state.value.isLoading) {
            return@launch
        }

        _state.update { it.copy(isLoading = true) }

        when (val result = orderRepository.fetchOrders()) {
            is Result.Failure -> Unit
            is Result.Success -> {
                _allOrders.update { result.data }
                _state.update { it.copy(orders = result.data) }
            }
        }

        _state.update { it.copy(isLoading = false) }
    }

    private fun updateItemsByQuery(query: String) {
        if (_state.value.filter != OrderFilter.ALL) {
            return
        }

        if (query.isBlank()) {
            _state.update { it.copy(orders = _allOrders.value) }
            return
        }

        val searchedOrdersByProductName = _allOrders.value.filter {
            it.productName.contains(query, true)
        }

        _state.update { it.copy(orders = searchedOrdersByProductName) }
    }

    private fun updateItemsByFilter(filter: OrderFilter) {
        val allOrders = _allOrders.value
        val filteredOrders = when (filter) {
            OrderFilter.ALL -> allOrders
            OrderFilter.PENDING -> allOrders.filter {
                it.status == OrderStatus.PENDING
            }
            OrderFilter.IN_TRANSIT -> allOrders.filter {
                it.status == OrderStatus.IN_TRANSIT
            }
            OrderFilter.COMPLETED -> allOrders.filter {
                it.status == OrderStatus.COMPLETED
            }
        }

        _state.update { it.copy(orders = filteredOrders) }
    }
}