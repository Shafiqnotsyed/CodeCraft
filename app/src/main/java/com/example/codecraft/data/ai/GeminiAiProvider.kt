package com.example.codecraft.data.ai

import android.util.Log
import com.example.codecraft.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay

class GeminiAiProvider : AiProvider {

    private fun buildModel(name: String) = GenerativeModel(
        modelName = name,
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private suspend fun generateWithRetry(prompt: String): String {
        val models = listOf(
            "gemini-2.5-flash",
            "gemini-1.5-flash" // fallback
        )

        var attempt = 1
        var delayMs = 600L
        var lastError: Throwable? = null

        while (attempt <= 5) {
            try {
                val modelName = if (attempt <= 3) models[0] else models[1]
                val model = buildModel(modelName)

                val resp = model.generateContent(content { text(prompt) })
                val text = resp.text
                if (!text.isNullOrBlank()) return text

                // No text but no error – treat as failure and retry
                lastError = IllegalStateException("Empty Gemini response")
            } catch (e: Exception) {
                lastError = e
                val msg = e.message.orEmpty()
                Log.e("GeminiAiProvider", "Gemini attempt $attempt failed: $msg", e)
            }

            // Wait then try again
            delay(delayMs)
            delayMs *= 2
            attempt++
        }

        Log.e("GeminiAiProvider", "Giving up after 5 attempts", lastError)
        return "The AI is currently busy or unavailable. Please wait a few seconds and try again."
    }

    override suspend fun feedbackForCode(
        language: String,
        code: String,
        diagnostics: String,
        lessonTitle: String,
        lessonDescription: String
    ): Result<String> = runCatching {
        val prompt = """
            You are a senior coding tutor for beginners.

            Language: $language
            Lesson: $lessonTitle

            Student code:
            $code

            Diagnostics:
            $diagnostics

            Give:
            - what's wrong (if anything)
            - a short hint
            - a tiny fix snippet if needed
            - one beginner tip
        """.trimIndent()

        generateWithRetry(prompt)
    }.onFailure { e ->
        Log.e("GeminiAiProvider", "Error generating feedback", e)
    }

    override suspend fun generateGeneralAnswer(
        language: String,
        question: String
    ): Result<String> = runCatching {
        val prompt = """
            A beginner has a question about $language.

            Question: $question

            Explain in simple terms and give a tiny example if it helps.
        """.trimIndent()

        generateWithRetry(prompt)
    }.onFailure { e ->
        Log.e("GeminiAiProvider", "Error generating answer", e)
    }
}