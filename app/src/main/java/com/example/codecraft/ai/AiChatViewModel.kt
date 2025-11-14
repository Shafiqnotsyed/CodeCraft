package com.example.codecraft.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.codecraft.data.ai.AiProvider
import com.example.codecraft.data.db.ChatMessage
import com.example.codecraft.data.db.ChatMessageDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_MESSAGES = 10   // ≈ last 5 prompts + 5 AI replies

class AiChatViewModel(
    private val chatMessageDao: ChatMessageDao,
    private val provider: AiProvider
) : ViewModel() {

    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid ?: ""

    // Only expose the last MAX_MESSAGES messages to the UI
    val history = chatMessageDao.getAllMessages(userId)
        .map { messages ->
            if (messages.size <= MAX_MESSAGES) messages
            else messages.takeLast(MAX_MESSAGES)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Loading flag to prevent multiple AI calls at once
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(language: String, question: String) {
        // Ignore extra taps / recompositions while a request is in flight
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Save the user's message
                val userMessage = ChatMessage(userId = userId, role = "user", text = question)
                chatMessageDao.insertMessage(userMessage)

                // 2. Call the AI for an answer
                val result = provider.generateGeneralAnswer(language, question)
                val aiResponse = result.getOrNull()
                    ?: "The AI is currently busy. Please wait a few seconds and try again."

                // 3. Save the AI's response
                val aiMessage = ChatMessage(userId = userId, role = "model", text = aiResponse)
                chatMessageDao.insertMessage(aiMessage)


            } finally {
                _isLoading.value = false
            }
        }
    }
}

class AiChatViewModelFactory(
    private val chatMessageDao: ChatMessageDao,
    private val aiProvider: AiProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AiChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiChatViewModel(chatMessageDao, aiProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}