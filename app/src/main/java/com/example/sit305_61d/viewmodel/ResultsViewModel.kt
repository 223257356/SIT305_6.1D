package com.example.sit305_61d.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log

// Updated model to hold necessary data for displaying results
data class ResultItemUiModel(
    val questionNumber: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerLetter: String,
    // Add userSelectedOptionIndex: Int? if we pass/save user answers
)

// Updated state to hold the results data
data class ResultsUiState(
    val quizTitle: String = "",
    val results: List<ResultItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ResultsViewModel(
    // Remove SavedStateHandle from constructor
    // Add any other dependencies if needed (e.g., a repository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private var topic: String = ""

    init {
        Log.d("ResultsViewModel", "ViewModel initialized.")
        // Don't load data here anymore
    }

    // New function to initialize and load data
    fun initializeResults(taskId: String) {
        Log.d("ResultsViewModel", "initializeResults called with taskId: '$taskId'")
        if (taskId.isBlank()) {
            Log.e("ResultsViewModel", "initializeResults called with blank taskId!")
            _uiState.update { it.copy(isLoading = false, errorMessage = "Topic (Task ID) missing") }
            return
        }
        // Prevent re-initialization if topic is already set and matches
        if (this.topic.isNotEmpty() && this.topic == taskId) {
            Log.w("ResultsViewModel", "initializeResults called again with the same taskId: $taskId. Ignoring.")
            return
        }
        this.topic = taskId
        loadResultsData(taskId)
    }

    private fun loadResultsData(topicToLoad: String) {
        viewModelScope.launch {
            Log.d("ResultsViewModel", "Starting load for topic: $topicToLoad")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            kotlinx.coroutines.delay(500) // Simulate loading delay

            // TODO: Replace with actual logic to fetch results/correct answers
            // For now, use dummy data based on the topic (taskId)
            try {
                val dummyCorrectAnswers = listOf(
                     ResultItemUiModel(1, "What is the capital of $topicToLoad (Dummy)?", listOf("Option A", "Option B", "Option C", "Option D"), "A"),
                     ResultItemUiModel(2, "Is $topicToLoad fun (Dummy)?", listOf("Yes", "No"), "A")
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        quizTitle = "Results: $topicToLoad",
                        results = dummyCorrectAnswers
                    )
                }
                 Log.d("ResultsViewModel", "Successfully loaded dummy results for $topicToLoad")
            } catch (e: Exception) {
                 Log.e("ResultsViewModel", "Failed to load results for $topicToLoad", e)
                 _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load results: ${e.message}") }
            }
        }
    }

    // Call this after error message has been shown
    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
