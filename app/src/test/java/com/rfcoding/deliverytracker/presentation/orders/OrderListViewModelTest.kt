package com.rfcoding.deliverytracker.presentation.orders

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.rfcoding.deliverytracker.MainCoroutineRule
import com.rfcoding.deliverytracker.data.FakeOrderRepository
import com.rfcoding.deliverytracker.domain.Order
import com.rfcoding.deliverytracker.domain.OrderStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class OrderListViewModelTest {

    private lateinit var repository: FakeOrderRepository
    private lateinit var viewModel: OrderListViewModel

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        repository = FakeOrderRepository(
            now = ZonedDateTime.now()
        )

        viewModel = OrderListViewModel(repository)
    }

    @Test
    fun `initial state is empty`() = runTest {
        viewModel.state.test {
            // Initial state
            val state = awaitItem()

            assertThat(state.isLoading).isFalse()
            assertThat(state.orders).isEmpty()
            assertThat(state.query).isEmpty()
            assertThat(state.filter).isEqualTo(OrderFilter.ALL)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load orders successfully`() = runTest {
        viewModel.state.test {
            val initial = awaitItem()
            assertThat(initial.isLoading).isFalse()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val finalState = awaitItem()
            assertThat(finalState.orders).isNotEmpty()
            assertThat(finalState.isLoading).isFalse()
        }
    }

    @Test
    fun `load orders with error keeps orders empty`() = runTest {
        repository.showError = true

        viewModel.state.test {
            val initial = awaitItem()
            assertThat(initial.isLoading).isFalse()

            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val finalState = awaitItem()
            assertThat(finalState.orders).isEmpty()
            assertThat(finalState.isLoading).isFalse()
        }
    }

    @Test
    fun `refresh loads orders again`() = runTest {
        viewModel.state.test {
            // Skip until loadOrders() is done
            advanceUntilIdle()

            viewModel.onAction(OrderListAction.OnRefresh)
            advanceUntilIdle()

            val refreshedState = expectMostRecentItem()

            assertThat(refreshedState.isLoading).isFalse()
            assertThat(refreshedState.orders).isNotEmpty()
        }
    }

    @Test
    fun `filter pending orders`() = runTest {
        viewModel.state.test {
            // Skip until loadOrders() is done
            advanceUntilIdle()

            viewModel.onAction(
                OrderListAction.OnFilterChanged(OrderFilter.PENDING)
            )
            advanceUntilIdle()

            val filteredState = expectMostRecentItem()

            assertThat(filteredState.filter).isEqualTo(OrderFilter.PENDING)
            assertThat(filteredState.query).isEmpty()

            val isAllPending = filteredState.orders.all { it.status == OrderStatus.PENDING }
            assertThat(isAllPending).isTrue()
            assertThat(filteredState.orders).isNotEmpty()
        }
    }

    private fun insertOrder(block: (lastOrder: Order) -> Order) {
        repository.dummyOrders.add(
            block(repository.dummyOrders.last())
        )
    }

    @Test
    fun `filter in transit orders`() = runTest {
        insertOrder { lastOrder ->
            lastOrder.copy(id = "6", productName = "Product #6", status = OrderStatus.IN_TRANSIT)
        }
        viewModel.state.test {
            // Skip until loadOrders() is done
            advanceUntilIdle()

            viewModel.onAction(
                OrderListAction.OnFilterChanged(OrderFilter.IN_TRANSIT)
            )

            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertThat(state.filter).isEqualTo(OrderFilter.IN_TRANSIT)
            assertThat(state.query).isEmpty()

            val isAllInTransit = state.orders.all { it.status == OrderStatus.IN_TRANSIT }
            assertThat(isAllInTransit).isTrue()
            assertThat(state.orders).isNotEmpty()
        }
    }

    @Test
    fun `filter completed orders`() = runTest {
        insertOrder { lastOrder ->
            lastOrder.copy(id = "6", productName = "Product #6", status = OrderStatus.COMPLETED)
        }
        viewModel.state.test {
            // Skip until loadOrders() is done
            advanceUntilIdle()

            viewModel.onAction(
                OrderListAction.OnFilterChanged(OrderFilter.COMPLETED)
            )

            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertThat(state.filter).isEqualTo(OrderFilter.COMPLETED)
            assertThat(state.query).isEmpty()

            val isAllCompleted = state.orders.all { it.status == OrderStatus.COMPLETED }
            assertThat(isAllCompleted).isTrue()
            assertThat(state.orders).isNotEmpty()
        }
    }

    @Test
    fun `selecting filter clears search query`() = runTest {
        viewModel.state.test {
            // Skip until loadOrders() is done
            advanceUntilIdle()

            viewModel.onAction(
                OrderListAction.OnSearchQueryChanged("coffee")
            )
            advanceUntilIdle()

            val searchState = expectMostRecentItem()
            assertThat(searchState.query).isEqualTo("coffee")

            viewModel.onAction(
                OrderListAction.OnFilterChanged(OrderFilter.PENDING)
            )
            advanceUntilIdle()

            val filterState = expectMostRecentItem()

            assertThat(filterState.query).isEmpty()
            assertThat(filterState.filter).isEqualTo(OrderFilter.PENDING)
        }
    }

    @Test
    fun `search query selects matching orders`() = runTest {
        viewModel.state.test {
            // Skip until loadOrders() is done
            advanceUntilIdle()

            viewModel.onAction(
                OrderListAction.OnSearchQueryChanged("product #3")
            )
            advanceUntilIdle()

            val state = expectMostRecentItem()

            assertThat(state.query).isEqualTo("product #3")
            assertThat(state.filter).isEqualTo(OrderFilter.ALL)

            val searchedOrders = state.orders.all {
                it.productName.contains("product #3", true)
            }
            assertThat(searchedOrders).isTrue()
            assertThat(state.orders).isNotEmpty()
        }
    }

    @Test
    fun `blank search query restores all orders`() = runTest {
        viewModel.state.test {
            // Skip until loadOrders() is done
            advanceUntilIdle()

            val allOrders = expectMostRecentItem().orders

            viewModel.onAction(
                OrderListAction.OnSearchQueryChanged("coffee")
            )
            advanceUntilIdle()

            // The list should be empty because in FakeOrderRepository we don't have a product that contains 'coffee' in the name.
            val updatedState = expectMostRecentItem()
            assertThat(updatedState.orders).isEmpty()
            assertThat(updatedState.query).isEqualTo("coffee")

            viewModel.onAction(
                OrderListAction.OnSearchQueryChanged("")
            )
            advanceUntilIdle()

            val finalState = expectMostRecentItem()
            assertThat(finalState.orders).isEqualTo(allOrders)
        }
    }

    @Test
    fun `search query resets filter to all`() = runTest {
        viewModel.state.test {
            // Skip until loadOrders() is done
            advanceUntilIdle()

            viewModel.onAction(
                OrderListAction.OnFilterChanged(OrderFilter.PENDING)
            )
            advanceUntilIdle()

            val updatedState = expectMostRecentItem()
            assertThat(updatedState.filter).isEqualTo(OrderFilter.PENDING)

            viewModel.onAction(
                OrderListAction.OnSearchQueryChanged("coffee")
            )
            advanceUntilIdle()

            val mostRecentState = expectMostRecentItem()

            assertThat(mostRecentState.filter).isEqualTo(OrderFilter.ALL)
            assertThat(mostRecentState.query).isEqualTo("coffee")

            // Since we have no product that contains 'coffee'
            assertThat(mostRecentState.orders).isEmpty()
        }
    }
}