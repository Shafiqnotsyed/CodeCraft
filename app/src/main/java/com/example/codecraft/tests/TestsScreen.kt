package com.example.codecraft.tests

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codecraft.data.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsScreen(viewModel: TestsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current as Activity

    if (uiState.showCompletionScreen) {
        TestCompletionScreen(
            score = uiState.lastTestScore,
            totalQuestions = uiState.lastTestTotalQuestions,
            onAnimationFinished = { viewModel.onAnimationFinished() }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = if (uiState.currentTestCategory == null) "Select a Test" else uiState.currentTestCategory!!)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (uiState.currentTestCategory != null) {
                                viewModel.clearSelectedCategory()
                            } else {
                                context.finish()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.currentTestCategory == null) {
                    TestSelectionScreen(
                        languageGroups = uiState.groupedAndFilteredLanguages,
                        onCategorySelected = { viewModel.selectTestCategory(it) }
                    )
                } else {
                    IndividualTestScreen(
                        questions = uiState.currentTestQuestions,
                        userAnswers = uiState.userAnswers,
                        isTestSubmitted = uiState.isTestSubmitted,
                        finalScore = uiState.lastTestScore,
                        onAnswerSelected = { q, a -> viewModel.onAnswerSelected(q, a) },
                        onSubmit = { viewModel.submitTest() },
                        onRetake = { viewModel.resetTest() }
                    )
                }
            }
        }
    }
}

@Composable
fun TestSelectionScreen(languageGroups: List<LanguageTestGroup>, onCategorySelected: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        languageGroups.forEach { group ->
            item {
                Text(
                    text = group.language,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(group.tests) { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onCategorySelected(result.category) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = result.category,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (result.score != null) {
                            val percentage = (result.score.toFloat() / result.totalQuestions.toFloat() * 100).toInt()
                            Text(text = "$percentage%", style = MaterialTheme.typography.bodyLarge)
                        } else {
                            Text(text = "Not Attempted", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IndividualTestScreen(
    questions: List<Question>,
    userAnswers: Map<String, String>,
    isTestSubmitted: Boolean,
    finalScore: Int,
    onAnswerSelected: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onRetake: () -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(questions) { question ->
            QuestionItem(
                question = question,
                selectedAnswer = userAnswers[question.id],
                onAnswerSelected = { answer -> onAnswerSelected(question.id, answer) },
                isTestSubmitted = isTestSubmitted
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            if (isTestSubmitted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Your score: $finalScore / ${questions.size}", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetake) {
                        Text(text = "Retake Test")
                    }
                }
            } else {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Submit")
                }
            }
        }
    }
}

@Composable
fun QuestionItem(
    question: Question,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    isTestSubmitted: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = question.questionText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            question.options.forEach { option ->
                val isCorrect = isTestSubmitted && option == question.correctAnswer
                val isSelected = selectedAnswer == option

                val radioColor = when {
                    !isTestSubmitted -> MaterialTheme.colorScheme.secondary
                    isCorrect -> Color.Green
                    isSelected && !isCorrect -> Color.Red
                    else -> Color.Gray
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isTestSubmitted) { onAnswerSelected(option) }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onAnswerSelected(option) },
                        enabled = !isTestSubmitted,
                        colors = RadioButtonDefaults.colors(selectedColor = radioColor, unselectedColor = Color.Gray)
                    )
                    Text(text = option, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
