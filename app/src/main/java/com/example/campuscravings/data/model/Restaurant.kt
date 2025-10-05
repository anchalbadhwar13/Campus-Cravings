package com.example.campuscravings.data.model

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val rating: Double = 0.0,
    val deliveryFee: Double = 2.99,
    val estimatedTime: String = "20-30 min",
    val isOpen: Boolean = true
)
