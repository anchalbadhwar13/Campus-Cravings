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
            
            // Map documents and ensure the document ID is set as the id field
            val restaurants = snapshot.documents.map { doc ->
                val restaurant = doc.toObject(Restaurant::class.java) ?: Restaurant()
                restaurant.copy(id = doc.id) // Ensure document ID is set
            }
            
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
            android.util.Log.d("RestaurantRepository", "Fetching menu items for restaurant: $restaurantId")
            val snapshot = firestore.collection("menuItems")
                .whereEqualTo("restaurantId", restaurantId)
                .whereEqualTo("isAvailable", true)
                .get()
                .await()
            
            // Map documents and ensure the document ID is set as the id field
            val items = snapshot.documents.map { doc ->
                val item = doc.toObject(MenuItem::class.java) ?: MenuItem()
                item.copy(id = doc.id) // Ensure document ID is set
            }
            
            android.util.Log.d("RestaurantRepository", "Found ${items.size} menu items")
            items.forEach { item ->
                android.util.Log.d("RestaurantRepository", "  - ${item.name} (id: ${item.id}, restaurantId: ${item.restaurantId})")
            }
            
            Result.success(items)
        } catch (e: Exception) {
            android.util.Log.e("RestaurantRepository", "Error fetching menu items", e)
            Result.failure(e)
        }
    }
}
