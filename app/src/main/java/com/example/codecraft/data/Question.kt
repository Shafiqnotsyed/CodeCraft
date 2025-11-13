package com.example.codecraft.data

data class Question(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String
)
