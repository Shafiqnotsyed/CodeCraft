package com.example.codecraft.data.ai

interface AiProvider {
    suspend fun feedbackForCode(
        language: String,
        code: String,
        diagnostics: String,
        lessonTitle: String,
        lessonDescription: String
    ): Result<String>

    suspend fun generateGeneralAnswer(
        language: String,
        question: String
    ): Result<String>
}
