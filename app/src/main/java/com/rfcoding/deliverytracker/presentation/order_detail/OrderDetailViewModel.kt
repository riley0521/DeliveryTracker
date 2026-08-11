package com.rfcoding.deliverytracker.presentation.order_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rfcoding.deliverytracker.domain.OrderRepository
import com.rfcoding.deliverytracker.domain.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    private val orderRepository: OrderRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val id = savedStateHandle.get<String>("id")!!

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(OrderDetailState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                refreshEveryTenSeconds()
                getOrderDetail()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = OrderDetailState()
        )

    private var refreshJob: Job? = null

    private fun refreshEveryTenSeconds() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(10_000L)
                getOrderDetail()
            }
        }
    }

    private suspend fun getOrderDetail() {
        _state.update { it.copy(isLoading = true) }
        delay(3_000L)

        when (val result = orderRepository.getOrderById(id)) {
            is Result.Failure -> Unit
            is Result.Success -> {
                _state.update { it.copy(order = result.data) }
            }
        }

        _state.update { it.copy(isLoading = false) }
    }

}