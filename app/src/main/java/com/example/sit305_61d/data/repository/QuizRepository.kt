package com.example.sit305_61d.data.repository

import com.example.sit305_61d.data.network.ApiService
import com.example.sit305_61d.data.network.RetrofitClient
import com.example.sit305_61d.data.network.dto.QuizQuestionDto

// Interface for the repository (optional but good practice)
interface QuizRepository {
    suspend fun getQuiz(topic: String): Result<List<QuizQuestionDto>> // Use Result wrapper for error handling
}

// Implementation moved to QuizRepositoryImpl.kt
// class QuizRepositoryImpl(...) { ... }
