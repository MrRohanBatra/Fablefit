package com.rohan.fablefit.ui.Order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.fablefit.ui.Order.OrderRepository
import com.rohan.fablefit.ui.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// -----------------------------
// 📦 UI STATE
// -----------------------------
sealed class OrderUiState {
    object Idle : OrderUiState()
    object Loading : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
    data class Error(val message: String) : OrderUiState()
}


// -----------------------------
// 🔍 TRACK STATE (separate)
// -----------------------------
sealed class TrackOrderState {
    object Idle : TrackOrderState()
    object Loading : TrackOrderState()
    data class Success(val data: OrderTrackResponse) : TrackOrderState()
    data class Error(val message: String) : TrackOrderState()
}


// -----------------------------
// 🧠 VIEWMODEL
// -----------------------------
class OrderViewModel(

) : ViewModel() {
    private val repository: OrderRepository= OrderRepository()
    // 🔹 Orders list state
    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Idle)
    val uiState: StateFlow<OrderUiState> = _uiState

    // 🔹 Track order state
    private val _trackState = MutableStateFlow<TrackOrderState>(TrackOrderState.Idle)
    val trackState: StateFlow<TrackOrderState> = _trackState

    // 🔹 Place order result
    private val _placeOrderResult = MutableStateFlow<OrderPlaceResponse?>(null)
    val placeOrderResult: StateFlow<OrderPlaceResponse?> = _placeOrderResult

    // 🔹 Cancel result
    private val _cancelResult = MutableStateFlow<OrderCancelResponse?>(null)
    val cancelResult: StateFlow<OrderCancelResponse?> = _cancelResult


    // -----------------------------
    // 📦 GET USER ORDERS
    // -----------------------------
    fun loadOrders(userId: String) {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading

            val result = repository.getUserOrders(userId)

            result.onSuccess {
                _uiState.value = OrderUiState.Success(it.orders)
            }.onFailure {
                _uiState.value = OrderUiState.Error(it.message ?: "Error loading orders")
            }
        }
    }


    // -----------------------------
    // 🧾 PLACE ORDER
    // -----------------------------
    fun placeOrder(userId: String, address: String) {
        viewModelScope.launch {
            val result = repository.placeOrder(userId, address)

            result.onSuccess {
                _placeOrderResult.value = it
            }.onFailure {
                _uiState.value = OrderUiState.Error(it.message ?: "Failed to place order")
            }
        }
    }


    // -----------------------------
    // 🔍 TRACK ORDER
    // -----------------------------
    fun trackOrder(orderId: String) {
        viewModelScope.launch {
            _trackState.value = TrackOrderState.Loading

            val result = repository.trackOrder(orderId)

            result.onSuccess {
                _trackState.value = TrackOrderState.Success(it)
            }.onFailure {
                _trackState.value = TrackOrderState.Error(it.message ?: "Tracking failed")
            }
        }
    }


    // -----------------------------
    // ❌ CANCEL ORDER
    // -----------------------------
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            val result = repository.cancelOrder(orderId)

            result.onSuccess {
                _cancelResult.value = it

                // 🔥 Update UI instantly (optional but useful)
                val current = _uiState.value
                if (current is OrderUiState.Success) {
                    val updated = current.orders.map {
                        if (it.order_id == orderId) {
                            it.copy(status = "cancelled")
                        } else it
                    }
                    _uiState.value = OrderUiState.Success(updated)
                }

            }.onFailure {
                _uiState.value = OrderUiState.Error(it.message ?: "Cancel failed")
            }
        }
    }
}