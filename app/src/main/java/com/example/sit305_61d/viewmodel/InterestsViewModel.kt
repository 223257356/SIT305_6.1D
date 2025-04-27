package com.example.sit305_61d.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents the state of the Interests UI
data class InterestsUiState(
    val availableInterests: List<String> = emptyList(),
    val selectedInterests: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToHome: Boolean = false // Flag to trigger navigation
)

class InterestsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InterestsUiState())
    val uiState: StateFlow<InterestsUiState> = _uiState.asStateFlow()

    private val maxSelection = 10

    init {
        fetchInterests()
    }

    private fun fetchInterests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            kotlinx.coroutines.delay(1000) // Simulate network delay

            // TODO: Replace with actual API call to get interests
            val interests = listOf(
                "Algorithms", "Data Structures", "Web Development", "Testing",
                "Machine Learning", "AI Ethics", "Cloud Computing", "Cybersecurity",
                "Mobile Dev", "UI/UX Design", "Game Development", "DevOps",
                "Databases", "Networking", "Operating Systems"
            )

            _uiState.update { it.copy(isLoading = false, availableInterests = interests) }
            // Handle potential errors from API call here
        }
    }

    fun onInterestSelected(interest: String) {
        val currentSelected = _uiState.value.selectedInterests
        val newSelected = if (currentSelected.contains(interest)) {
            currentSelected - interest
        } else if (currentSelected.size < maxSelection) {
            currentSelected + interest
        } else {
            // Optional: Show a message if max selection is reached
            // _uiState.update { it.copy(errorMessage = "You can select up to $maxSelection interests") }
            currentSelected // No change if max is reached
        }
        _uiState.update { it.copy(selectedInterests = newSelected, errorMessage = null) }
    }

    fun onNextClicked() {
        if (_uiState.value.selectedInterests.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please select at least one interest") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            kotlinx.coroutines.delay(500) // Simulate saving preferences

            // TODO: Implement logic to save selected interests (API call)
            println("Selected Interests: ${_uiState.value.selectedInterests}")

            _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
            // Handle potential errors here
        }
    }

     // Call this after navigation has occurred
    fun onNavigationHandled() {
        _uiState.update { it.copy(navigateToHome = false) }
    }

    // Call this after error message has been shown
    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
