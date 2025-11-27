package com.example.codecraft.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.codecraft.CodeCraftApplication
import com.example.codecraft.ai.AiChatViewModel
import com.example.codecraft.ai.AiChatViewModelFactory
import com.example.codecraft.ui.theme.CodeCraftTheme
import com.example.codecraft.userinterface.screens.chat.AiChatScreen

class AiChatActivity : ComponentActivity() {

    private val viewModel: AiChatViewModel by viewModels {
        val container = (application as CodeCraftApplication).container
        AiChatViewModelFactory(
            chatMessageDao = container.appDatabase.chatMessageDao(),
            aiProvider = container.aiProvider
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeCraftTheme {
                AiChatScreen(viewModel = viewModel)
            }
        }
    }
}
