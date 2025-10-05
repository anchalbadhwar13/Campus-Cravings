package com.example.campuscravings.data.repository

import com.example.campuscravings.data.model.MenuItem
import com.example.campuscravings.data.model.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    suspend fun getRestaurants(): Result<List<Restaurant>> {
        return try {
            val snapshot = firestore.collection("restaurants")
                .whereEqualTo("isOpen", true)
                .get()
                .await()
            val restaurants = snapshot.toObjects(Restaurant::class.java)
            Result.success(restaurants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRestaurantById(restaurantId: String): Result<Restaurant> {
        return try {
            val doc = firestore.collection("restaurants")
                .document(restaurantId)
                .get()
                .await()
            val restaurant = doc.toObject(Restaurant::class.java) 
                ?: throw Exception("Restaurant not found")
            Result.success(restaurant)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMenuItems(restaurantId: String): Result<List<MenuItem>> {
        return try {
            val snapshot = firestore.collection("menuItems")
                .whereEqualTo("restaurantId", restaurantId)
                .whereEqualTo("isAvailable", true)
                .get()
                .await()
            val items = snapshot.toObjects(MenuItem::class.java)
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
