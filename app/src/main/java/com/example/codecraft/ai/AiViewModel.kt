package com.example.codecraft.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codecraft.data.ai.AiProvider
import com.example.codecraft.data.ai.GeminiAiProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiViewModel(
    private val provider: AiProvider = GeminiAiProvider()
) : ViewModel() {

    private val _ui = MutableStateFlow(AiUiState())
    val ui: StateFlow<AiUiState> = _ui.asStateFlow()

    fun askForFeedback(
        language: String,
        code: String,
        diagnostics: String,
        lessonTitle: String,
        lessonDescription: String
    ) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null, answer = null)
            val result = provider.feedbackForCode(
                language = language,
                code = code,
                diagnostics = diagnostics,
                lessonTitle = lessonTitle,
                lessonDescription = lessonDescription
            )
            _ui.value = _ui.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message,
                answer = result.getOrNull()
            )
        }
    }

    fun reset() { _ui.value = AiUiState() }
}
