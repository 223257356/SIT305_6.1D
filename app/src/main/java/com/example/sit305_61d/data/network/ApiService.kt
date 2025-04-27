package com.example.sit305_61d.data.network

import com.example.sit305_61d.data.network.dto.QuizApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("getQuiz")
    suspend fun getQuiz(
        @Query("topic") topic: String
    ): QuizApiResponse // Return the DTO that wraps the list

    // Add other endpoints here later (e.g., login, signup, submit) if they are created

}
