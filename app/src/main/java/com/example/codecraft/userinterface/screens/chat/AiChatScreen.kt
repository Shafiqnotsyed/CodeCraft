package com.example.codecraft.userinterface.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.codecraft.ai.AiChatViewModel
import com.example.codecraft.data.db.ChatMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(viewModel: AiChatViewModel) {
    val history by viewModel.history.collectAsState()
    val languages = listOf("Python", "Java", "HTML", "General")
    var selectedLanguage by remember { mutableStateOf(languages[0]) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(history.size) {
        coroutineScope.launch {
            if (history.isNotEmpty()) {
                listState.animateScrollToItem(history.size - 1)
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Ask Craft", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomInputBar(viewModel, selectedLanguage, onLanguageSelected = { selectedLanguage = it })
        }
    ) { paddingValues ->
        val backgroundBrush = Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                MaterialTheme.colorScheme.background
            )
        )

        Box(modifier = Modifier.background(backgroundBrush)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(history) { message ->
                    MessageBubble(message)
                }
            }
        }
    }
}

@Composable
fun BottomInputBar(
    viewModel: AiChatViewModel,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    var question by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Pushes the card above the system navigation bar
            .imePadding(),           // Pushes the card above the keyboard
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanguageSelector(selectedLanguage = selectedLanguage, onLanguageSelected = onLanguageSelected)

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask a question...") },
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (question.isNotBlank()) {
                        viewModel.sendMessage(selectedLanguage, question)
                        question = ""
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


@Composable
fun LanguageSelector(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val languages = listOf("Python", "Java", "HTML", "General")

    Box {
        OutlinedButton(
            onClick = { isMenuExpanded = true },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(selectedLanguage)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Language")
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            languages.forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang) },
                    onClick = {
                        onLanguageSelected(lang)
                        isMenuExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(chatMessage: ChatMessage) {
    val isUser = chatMessage.role == "user"
    val horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isUser) {
            Icon(
                imageVector = Icons.Default.Forum,
                contentDescription = "AI Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp).padding(end = 8.dp)
            )
        }
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleColor)
        ) {
            Text(
                text = chatMessage.text,
                color = textColor,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
