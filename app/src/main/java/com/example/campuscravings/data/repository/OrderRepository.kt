package com.example.campuscravings.data.repository

import com.example.campuscravings.data.model.Order
import com.example.campuscravings.data.model.OrderStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val docRef = firestore.collection("orders").document()
            val orderWithId = order.copy(id = docRef.id)
            docRef.set(orderWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val doc = firestore.collection("orders")
                .document(orderId)
                .get()
                .await()
            val order = doc.toObject(Order::class.java) 
                ?: throw Exception("Order not found")
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCustomerOrders(customerId: String): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("customerId", customerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        
        awaitClose { listener.remove() }
    }
    
    fun getAvailableOrders(): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("status", OrderStatus.PENDING.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        
        awaitClose { listener.remove() }
    }
    
    fun getDeliveryPersonOrders(deliveryPersonId: String): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("deliveryPersonId", deliveryPersonId)
            .whereIn("status", listOf(OrderStatus.ACCEPTED.name, OrderStatus.PICKED_UP.name))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun acceptOrder(orderId: String, deliveryPersonId: String, deliveryPersonName: String): Result<Unit> {
        return try {
            firestore.collection("orders").document(orderId).update(
                mapOf(
                    "deliveryPersonId" to deliveryPersonId,
                    "deliveryPersonName" to deliveryPersonName,
                    "status" to OrderStatus.ACCEPTED.name,
                    "acceptedAt" to Timestamp.now()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )
            
            if (status == OrderStatus.DELIVERED) {
                updates["completedAt"] = Timestamp.now()
                
                // Update delivery person's earnings
                val order = getOrderById(orderId).getOrNull()
                if (order != null && order.deliveryPersonId != null) {
                    updateDeliveryPersonEarnings(order.deliveryPersonId, order.deliveryFee)
                }
            }
            
            firestore.collection("orders").document(orderId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateDeliveryPersonEarnings(deliveryPersonId: String, amount: Double) {
        try {
            val userRef = firestore.collection("users").document(deliveryPersonId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentEarnings = snapshot.getDouble("totalEarnings") ?: 0.0
                transaction.update(userRef, "totalEarnings", currentEarnings + amount)
            }.await()
            android.util.Log.d("OrderRepository", "Updated earnings for $deliveryPersonId: +$$amount")
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Failed to update earnings", e)
        }
    }
    
    suspend fun getCompletedOrdersCount(deliveryPersonId: String): Result<Int> {
        return try {
            val snapshot = firestore.collection("orders")
                .whereEqualTo("deliveryPersonId", deliveryPersonId)
                .whereEqualTo("status", OrderStatus.DELIVERED.name)
                .get()
                .await()
            Result.success(snapshot.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
