package com.example.campuscravings.data.model

import com.google.firebase.Timestamp

data class Order(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val deliveryPersonId: String? = null,
    val deliveryPersonName: String? = null,
    val restaurantId: String = "",
    val restaurantName: String = "",
    val restaurantLocation: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 2.99,
    val total: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val deliveryLocation: String = "",
    val specialInstructions: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val acceptedAt: Timestamp? = null,
    val completedAt: Timestamp? = null
)

data class OrderItem(
    val menuItemId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1
)

enum class OrderStatus {
    PENDING,        // Order placed, waiting for delivery person
    ACCEPTED,       // Delivery person accepted
    PICKED_UP,      // Food picked up from restaurant
    DELIVERED,      // Order completed
    CANCELLED       // Order cancelled
}
