package com.example.campuscravings.ui.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscravings.data.model.Order
import com.example.campuscravings.data.model.OrderStatus
import com.example.campuscravings.data.repository.AuthRepository
import com.example.campuscravings.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _availableOrders = MutableStateFlow<List<Order>>(emptyList())
    val availableOrders: StateFlow<List<Order>> = _availableOrders.asStateFlow()
    
    private val _activeOrders = MutableStateFlow<List<Order>>(emptyList())
    val activeOrders: StateFlow<List<Order>> = _activeOrders.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadAvailableOrders()
        loadActiveOrders()
    }
    
    private fun loadAvailableOrders() {
        viewModelScope.launch {
            orderRepository.getAvailableOrders().collect { orders ->
                _availableOrders.value = orders
            }
        }
    }
    
    private fun loadActiveOrders() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            orderRepository.getDeliveryPersonOrders(userId).collect { orders ->
                _activeOrders.value = orders
            }
        }
    }
    
    fun acceptOrder(order: Order) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            
            if (user == null) {
                _error.value = "User not found"
                _isLoading.value = false
                return@launch
            }
            
            val result = orderRepository.acceptOrder(order.id, user.id, user.name)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
    
    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = orderRepository.updateOrderStatus(orderId, status)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
