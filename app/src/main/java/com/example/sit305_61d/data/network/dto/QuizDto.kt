package com.example.sit305_61d.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizApiResponse(
    @SerialName("quiz") // Matches the JSON key
    val quiz: List<QuizQuestionDto>
)

@Serializable
data class QuizQuestionDto(
    @SerialName("question")
    val question: String,

    @SerialName("options")
    val options: List<String>, // The JSON returns an array/list of strings

    @SerialName("correct_answer")
    val correctAnswerLetter: String // e.g., "A", "B"
)
