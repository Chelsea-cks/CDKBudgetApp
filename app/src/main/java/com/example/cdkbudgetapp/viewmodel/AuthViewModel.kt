package com.example.cdkbudgetapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdkbudgetapp.data.AppDatabase
import com.example.cdkbudgetapp.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).userDao()

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val id = dao.register(User(email, password))
            if (id != -1L) {
                _authState.value = AuthResult.Success
            } else {
                _authState.value = AuthResult.Error("User already exists")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val user = dao.login(email, password)
            if (user != null) {
                _authState.value = AuthResult.Success
            } else {
                _authState.value = AuthResult.Error("Invalid email or password")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthResult.Idle
    }
}

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
