package com.example.campuscravings.data.repository

import com.example.campuscravings.data.model.User
import com.example.campuscravings.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    suspend fun signUp(email: String, password: String, name: String, phone: String, role: UserRole): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")
            
            val user = User(
                id = userId,
                name = name,
                email = email,
                phone = phone,
                role = role
            )
            
            firestore.collection("users").document(userId).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")
            
            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User data not found")
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(): User? {
        return try {
            val userId = getCurrentUserId() ?: return null
            val userDoc = firestore.collection("users").document(userId).get().await()
            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun updateUserLocation(location: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not logged in")
            firestore.collection("users").document(userId)
                .update("currentLocation", location).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateDeliveryAvailability(isAvailable: Boolean): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: throw Exception("User not logged in")
            firestore.collection("users").document(userId)
                .update("isAvailable", isAvailable).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
}
