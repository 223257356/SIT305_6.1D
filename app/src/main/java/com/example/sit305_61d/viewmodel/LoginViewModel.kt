package com.example.sit305_61d.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305_61d.data.UserDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents the state of the Login UI
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false // Flag to trigger navigation
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onLoginClicked() {
        val currentState = _uiState.value
        // Basic validation
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Username and password cannot be empty") }
            return
        }

        // Check credentials against UserDataStore
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Simulate delay

            // Find user returns Pair<User, String>? now
            val userDataPair = UserDataStore.findUserByUsername(currentState.username)

            // Check if user exists and password matches the one in the Pair
            val loginSuccessful = userDataPair != null && userDataPair.second == currentState.password

            if (loginSuccessful) {
                // Optionally, you could pass the User object (userDataPair.first) forward if needed
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid username or password") }
            }
        }
    }

    // Call this after navigation has occurred
    fun onLoginHandled() {
         _uiState.update { it.copy(loginSuccess = false) }
    }

    // Call this after error message has been shown
     fun onErrorMessageHandled() {
         _uiState.update { it.copy(errorMessage = null) }
     }
}
