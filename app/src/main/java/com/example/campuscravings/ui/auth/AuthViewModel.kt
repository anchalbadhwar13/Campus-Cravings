package com.example.campuscravings.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuscravings.data.model.User
import com.example.campuscravings.data.model.UserRole
import com.example.campuscravings.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        checkAuthStatus()
    }
    
    private fun checkAuthStatus() {
        viewModelScope.launch {
            if (authRepository.isUserLoggedIn()) {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Success(user)
                }
            }
        }
    }
    
    fun signUp(email: String, password: String, name: String, phone: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signUp(email, password, name, phone, role)
            _uiState.value = if (result.isSuccess) {
                val user = result.getOrNull()!!
                _currentUser.value = user
                AuthUiState.Success(user)
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Sign up failed")
            }
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signIn(email, password)
            _uiState.value = if (result.isSuccess) {
                val user = result.getOrNull()!!
                _currentUser.value = user
                AuthUiState.Success(user)
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Sign in failed")
            }
        }
    }
    
    fun signOut() {
        authRepository.signOut()
        _currentUser.value = null
        _uiState.value = AuthUiState.Initial
    }
    
    fun resetState() {
        _uiState.value = AuthUiState.Initial
    }
}

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
