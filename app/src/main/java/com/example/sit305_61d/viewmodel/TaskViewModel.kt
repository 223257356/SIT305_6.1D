package com.example.sit305_61d.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sit305_61d.data.repository.QuizRepository
import com.example.sit305_61d.data.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.delay

// Re-use or define data classes for task details and questions
data class TaskQuestionUiModel(val id: String, val questionText: String, val options: List<String>, val description: String? = null)
data class TaskDetailsUiModel(val id: String, val title: String, val description: String, val isAiGenerated: Boolean, val questions: List<TaskQuestionUiModel>)

// Represents the state of the Task UI
data class TaskUiState(
    val taskDetails: TaskDetailsUiModel? = null,
    val selectedAnswers: Map<String, Int> = emptyMap(), // Map<QuestionId, SelectedOptionIndex>
    val correctAnswers: Map<String, String> = emptyMap(), // Map<QuestionId, CorrectAnswerLetter>
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false, // Separate flag for submission loading state
    val errorMessage: String? = null,
    val navigateToResults: Boolean = false // Flag to trigger navigation
)

class TaskViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private var topic: String = ""

    init {
        Log.d("TaskViewModel", "ViewModel initialized.")
    }

    fun initializeTask(taskId: String) {
        Log.d("TaskViewModel", "initializeTask called with taskId: '$taskId'")
        if (taskId.isBlank()) {
            Log.e("TaskViewModel", "initializeTask called with blank taskId!")
            _uiState.update { it.copy(isLoading = false, errorMessage = "Topic (Task ID) missing") }
            return
        }
        if (this.topic.isNotEmpty() && this.topic == taskId) {
            Log.w("TaskViewModel", "initializeTask called again with the same taskId: $taskId. Ignoring.")
            return
        }
        this.topic = taskId

        // Launch the load directly here
        viewModelScope.launch {
            Log.d("TaskViewModel", "initializeTask scope started for $taskId")
            val topicToLoad = taskId // Use taskId directly

            // --- Start of loading logic ---
            if (_uiState.value.isLoading && this@TaskViewModel.topic == topicToLoad) {
                Log.d("TaskViewModel", "Already loading for $topicToLoad, skipping.")
                return@launch
            }
            Log.d("TaskViewModel", "Starting load for topic: $topicToLoad")
            Log.d("TaskViewModel", "Updating state: isLoading = true")
            _uiState.update { it.copy(isLoading = true, errorMessage = null, taskDetails = null) }
            Log.d("TaskViewModel", "State updated attempt: isLoading=${_uiState.value.isLoading}")

            val result = quizRepository.getQuiz(topicToLoad)
            Log.d("TaskViewModel", "quizRepository.getQuiz result received for $topicToLoad")

            result.onSuccess { questionsDto ->
                Log.d("TaskViewModel", "onSuccess for $topicToLoad. Question count: ${questionsDto.size}")
                if (questionsDto.isEmpty()) {
                     Log.w("TaskViewModel", "No questions returned for $topicToLoad.")
                     _uiState.update { it.copy(isLoading = false, errorMessage = "No questions generated for this topic.") }
                     return@onSuccess
                }
                // Map DTOs to UI Models
                val correctAnswersMap = mutableMapOf<String, String>()
                val questionsUiModel = questionsDto.mapIndexed { index, dto ->
                    val questionId = "q${index + 1}" // Generate a simple ID
                    correctAnswersMap[questionId] = dto.correctAnswerLetter // Store correct answer letter
                    TaskQuestionUiModel(
                        id = questionId,
                        questionText = dto.question,
                        options = dto.options,
                        description = null // API doesn't provide per-question description
                    )
                }
                val taskDetailsUiModel = TaskDetailsUiModel(
                    id = topicToLoad,
                    title = "Quiz: $topicToLoad",
                    description = "AI-generated quiz about $topicToLoad.",
                    isAiGenerated = true,
                    questions = questionsUiModel
                )
                Log.d("TaskViewModel", "Mapping successful for $topicToLoad. Updating state with task details.")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        taskDetails = taskDetailsUiModel,
                        correctAnswers = correctAnswersMap
                    )
                }
                Log.d("TaskViewModel", "State updated with taskDetails for $topicToLoad. isLoading=${_uiState.value.isLoading}")
            }

            result.onFailure { exception ->
                Log.e("TaskViewModel", "onFailure for $topicToLoad", exception)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load task: ${exception.message}") }
                Log.d("TaskViewModel", "State updated after failure for $topicToLoad. isLoading=${_uiState.value.isLoading}")
            }
            // --- End of loading logic ---
        }
    }

    fun onAnswerSelected(questionId: String, optionIndex: Int) {
        val currentAnswers = _uiState.value.selectedAnswers
        _uiState.update {
            it.copy(selectedAnswers = currentAnswers + (questionId to optionIndex))
        }
    }

    fun onSubmitClicked() {
        val state = _uiState.value
        if (state.taskDetails == null) return

        // API doesn't support submission, just navigate
        _uiState.update { it.copy(navigateToResults = true) }

        // Keep commented out block for future API integration
        /*
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            // TODO: Call actual submission API
            // Pass topic/taskId and selectedAnswers map
            println("Submitting answers for task $topic: ${state.selectedAnswers}")
            kotlinx.coroutines.delay(1500) // Simulate submission delay
            val submissionSuccessful = true // Assume success

            if (submissionSuccessful) {
                 _uiState.update { it.copy(isSubmitting = false, navigateToResults = true) }
            } else {
                _uiState.update { it.copy(isSubmitting = false, errorMessage = "Submission failed. Please try again.") }
            }
        }
        */
    }

     // Call this after navigation has occurred
    fun onSubmissionHandled() {
        _uiState.update { it.copy(navigateToResults = false) }
    }

    // Call this after error message has been shown
    fun onErrorMessageHandled() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Dummy data function removed
    // private fun findDummyTaskById(taskId: String): TaskDetailsUiModel? { ... }
}
