package com.example.codecraft.userinterface.ai

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codecraft.ai.AiViewModel

@Composable
fun AiAssist(
    language: String,
    code: String,
    diagnostics: String,
    lessonTitle: String,
    lessonDescription: String = "",
    viewModel: AiViewModel = viewModel()
) {
    val ui by viewModel.ui.collectAsState()

    val canAsk = remember(code, diagnostics) {
        // Only allow asking for feedback if there's some code
        // or a diagnostic message to analyze.
        code.isNotBlank() || diagnostics.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.Person, contentDescription = "AI Craft")
            Text("Ask Craft", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(8.dp))

        AnimatedContent(
            targetState = ui.isLoading,
            label = "AiAssistContent"
        ) { isLoading ->
            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            } else {
                Column {
                    if (ui.answer != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ui.answer!!,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                IconButton(onClick = { viewModel.reset() }) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Clear AI feedback"
                                    )
                                }
                            }
                        }
                    }

                    if (ui.error != null) {
                        Text(
                            text = "Error: ${ui.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (ui.answer == null) {
                        Button(
                            onClick = {
                                viewModel.askForFeedback(
                                    language = language,
                                    code = code,
                                    diagnostics = diagnostics,
                                    lessonTitle = lessonTitle,
                                    lessonDescription = lessonDescription
                                )
                            },
                            enabled = canAsk && ui.answer == null
                        ) {
                            Text("Ask for Feedback")
                        }
                    }
                }
            }
        }
    }
}
