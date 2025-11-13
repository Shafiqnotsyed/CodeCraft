package com.example.codecraft.userinterface.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codecraft.domain.rules.Exercise
import com.example.codecraft.userinterface.ai.AiAssist
import com.example.codecraft.userinterface.components.CodeEditor

// Beginner exercises for Python
private val pythonExercises = listOf(
    Exercise(
        id = "calc",
        title = "Build a Calculator",
        description = "Write a Python function that adds, subtracts, multiplies, and divides two numbers.",
        starterCode = """
            def calculator(a, b, op):
                # your code here
                pass
        """.trimIndent(),
        solutionCode = """
            def calculator(a, b, op):
                if op == '+': return a + b
                if op == '-': return a - b
                if op == '*': return a * b
                if op == '/': return a / b
        """.trimIndent()
    ),
    Exercise(
        id = "reverse",
        title = "Reverse a String",
        description = "Write a Python function that reverses a string.",
        starterCode = """
            def reverse_string(s):
                # your code here
                pass
        """.trimIndent(),
        solutionCode = """
            def reverse_string(s):
                return s[::-1]
        """.trimIndent()
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PythonExercisesScreen() {
    var selected by remember { mutableStateOf<Exercise?>(null) }
    var code by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var showSolution by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Python Exercises") }) }) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selected == null) {
                Text("Choose an exercise:", style = MaterialTheme.typography.titleMedium)
                pythonExercises.forEach { ex ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selected = ex
                                code = ex.starterCode
                                feedback = null
                                showSolution = false
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(ex.title, style = MaterialTheme.typography.titleLarge)
                            Text(ex.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                Text(selected!!.description, style = MaterialTheme.typography.titleLarge)

                CodeEditor(
                    value = code,
                    onChange = { code = it },
                    hint = "Type your Python code here..."
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            feedback =
                                if (code.filter { !it.isWhitespace() } ==
                                    selected!!.solutionCode.filter { !it.isWhitespace() }) {
                                    "✅ Correct!"
                                } else {
                                    "❌ Incorrect. Try again or view the solution."
                                }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Run Code") }

                    OutlinedButton(
                        onClick = { showSolution = true },
                        modifier = Modifier.weight(1f)
                    ) { Text("Show Answer") }
                }

                // ---------- AI Assist ----------
                val diagnosticsText = feedback ?: ""
                AiAssist(
                    language = "Python",
                    code = code,
                    diagnostics = diagnosticsText,
                    lessonTitle = selected?.title ?: "",
                    // lessonDescription omitted (default "")
                )

                feedback?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (it.contains("Correct"))
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }

                if (showSolution) {
                    Spacer(Modifier.height(8.dp))
                    Text("Solution:", style = MaterialTheme.typography.titleMedium)
                    Surface(
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            selected!!.solutionCode,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
