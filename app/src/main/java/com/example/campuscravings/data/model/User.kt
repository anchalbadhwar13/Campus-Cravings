package com.example.campuscravings.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val currentLocation: String = "", // Building name/location
    val isAvailable: Boolean = false, // For delivery people
    val activeOrderId: String? = null
)

enum class UserRole {
    CUSTOMER,
    DELIVERY
}
