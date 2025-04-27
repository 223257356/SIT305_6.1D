package com.example.sit305_61d.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305_61d.data.UserDataStore
import com.example.sit305_61d.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// Represents the state of the SignUp UI
data class SignUpUiState(
    val username: String = "",
    val email: String = "",
    val confirmEmail: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val signUpSuccess: Boolean = false // Flag to trigger navigation
)

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onConfirmEmailChanged(value: String) {
        _uiState.update { it.copy(confirmEmail = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun onPhoneNumberChanged(value: String) {
        _uiState.update { it.copy(phoneNumber = value, errorMessage = null) }
    }

    fun onSignUpClicked() {
        val state = _uiState.value

        // Basic Validation
        if (state.username.isBlank() || state.email.isBlank() || state.password.isBlank() || state.phoneNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields except confirm fields are required") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
             _uiState.update { it.copy(errorMessage = "Invalid email format") }
             return
        }
        if (state.email != state.confirmEmail) {
            _uiState.update { it.copy(errorMessage = "Emails do not match") }
            return
        }
        if (state.password.length < 6) { // Example: Minimum password length
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        // Attempt registration using UserDataStore
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Shorter delay for simulation

            // Create User object with only username and email
            val newUser = User(
                username = state.username,
                email = state.email
                // password and phoneNumber are not part of the User model
            )

            // Pass user object and password separately to the store
            val registrationSuccess = UserDataStore.registerUser(newUser, state.password)

            if (registrationSuccess) {
                _uiState.update { it.copy(isLoading = false, signUpSuccess = true) }
            } else {
                // Handle registration failure (e.g., username taken)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Username already exists.") }
            }
        }
    }

    // Call this after navigation has occurred
    fun onSignUpHandled() {
        _uiState.update { it.copy(signUpSuccess = false) }
    }

    // Call this after error message has been shown
    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
