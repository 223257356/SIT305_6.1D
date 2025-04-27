package com.example.sit305_61d.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Re-use or define data class for tasks displayed on Home screen
data class HomeTaskUiModel(val id: String, val title: String, val description: String, val isAiGenerated: Boolean, val isComplete: Boolean)

// Represents the state of the Home UI
data class HomeUiState(
    val userName: String = "",
    val tasks: List<HomeTaskUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() { // Allow manual refresh if needed later
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            kotlinx.coroutines.delay(1200) // Simulate network delay

            // TODO: Replace with actual API calls to get user info and tasks
            try {
                val userName = "Jessica" // Fetch actual user name
                val tasks = listOf(
                    HomeTaskUiModel(id = "Android Development", title = "Generated Task: Android", description = "Test your knowledge of Android Development.", isAiGenerated = true, isComplete = false),
                    HomeTaskUiModel(id = "task2", title = "Review Past Concepts", description = "Recap of topics covered last week", isAiGenerated = false, isComplete = true),
                    HomeTaskUiModel(id = "Kotlin Coroutines", title = "Generated Task: Coroutines", description = "Check your understanding of Kotlin Coroutines.", isAiGenerated = true, isComplete = false)
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = userName,
                        tasks = tasks
                    )
                }
            } catch (e: Exception) {
                // Handle potential errors from API calls
                 _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load data: ${e.message}") }
            }
        }
    }

    // Call this after error message has been shown
    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
