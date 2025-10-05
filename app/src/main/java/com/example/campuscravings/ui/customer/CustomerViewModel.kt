package com.example.campuscravings.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscravings.data.model.MenuItem
import com.example.campuscravings.data.model.Order
import com.example.campuscravings.data.model.OrderItem
import com.example.campuscravings.data.model.Restaurant
import com.example.campuscravings.data.repository.AuthRepository
import com.example.campuscravings.data.repository.OrderRepository
import com.example.campuscravings.data.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()
    
    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant: StateFlow<Restaurant?> = _selectedRestaurant.asStateFlow()
    
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()
    
    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cart: StateFlow<Map<String, Int>> = _cart.asStateFlow()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadRestaurants()
        loadOrders()
    }
    
    fun loadRestaurants() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = restaurantRepository.getRestaurants()
            if (result.isSuccess) {
                _restaurants.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
    
    fun selectRestaurant(restaurant: Restaurant) {
        _selectedRestaurant.value = restaurant
        _cart.value = emptyMap()
        _menuItems.value = emptyList() // Clear previous menu items immediately
        loadMenuItems(restaurant.id)
    }
    
    private fun loadMenuItems(restaurantId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            android.util.Log.d("CustomerViewModel", "Loading menu items for restaurant: $restaurantId")
            val result = restaurantRepository.getMenuItems(restaurantId)
            if (result.isSuccess) {
                val items = result.getOrNull() ?: emptyList()
                android.util.Log.d("CustomerViewModel", "Loaded ${items.size} menu items for restaurant: $restaurantId")
                items.forEach { item ->
                    android.util.Log.d("CustomerViewModel", "  - ${item.name} (${item.id}) for restaurant: ${item.restaurantId}")
                }
                _menuItems.value = items
            } else {
                android.util.Log.e("CustomerViewModel", "Error loading menu items: ${result.exceptionOrNull()?.message}")
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
    
    fun addToCart(menuItemId: String) {
        val currentCart = _cart.value.toMutableMap()
        currentCart[menuItemId] = (currentCart[menuItemId] ?: 0) + 1
        _cart.value = currentCart
    }
    
    fun removeFromCart(menuItemId: String) {
        val currentCart = _cart.value.toMutableMap()
        val currentQty = currentCart[menuItemId] ?: 0
        if (currentQty > 1) {
            currentCart[menuItemId] = currentQty - 1
        } else {
            currentCart.remove(menuItemId)
        }
        _cart.value = currentCart
    }
    
    fun getCartTotal(): Double {
        var total = 0.0
        _cart.value.forEach { (itemId, quantity) ->
            val item = _menuItems.value.find { it.id == itemId }
            if (item != null) {
                total += item.price * quantity
            }
        }
        return total
    }
    
    fun placeOrder(deliveryLocation: String, specialInstructions: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            val restaurant = _selectedRestaurant.value
            
            if (user == null || restaurant == null) {
                _error.value = "User or restaurant not found"
                _isLoading.value = false
                return@launch
            }
            
            val orderItems = _cart.value.map { (itemId, quantity) ->
                val menuItem = _menuItems.value.find { it.id == itemId }!!
                OrderItem(
                    menuItemId = itemId,
                    name = menuItem.name,
                    price = menuItem.price,
                    quantity = quantity
                )
            }
            
            val subtotal = getCartTotal()
            val total = subtotal + restaurant.deliveryFee
            
            val order = Order(
                customerId = user.id,
                customerName = user.name,
                restaurantId = restaurant.id,
                restaurantName = restaurant.name,
                restaurantLocation = restaurant.location,
                items = orderItems,
                subtotal = subtotal,
                deliveryFee = restaurant.deliveryFee,
                total = total,
                deliveryLocation = deliveryLocation,
                specialInstructions = specialInstructions
            )
            
            val result = orderRepository.createOrder(order)
            if (result.isSuccess) {
                val orderId = result.getOrNull()!!
                _cart.value = emptyMap()
                _selectedRestaurant.value = null
                _error.value = null
                onSuccess(orderId)
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
    
    private fun loadOrders() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            orderRepository.getCustomerOrders(userId).collect { orders ->
                _orders.value = orders
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
