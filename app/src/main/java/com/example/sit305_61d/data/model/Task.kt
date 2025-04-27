package com.example.sit305_61d.data.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val questions: List<Question>
)

data class Question(
    val id: String,
    val text: String,
    val options: List<String>? = null // For multiple choice
    // Add other question types/fields as needed
)
