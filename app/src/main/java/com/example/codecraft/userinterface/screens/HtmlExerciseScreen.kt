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

// Beginner HTML exercises
private val htmlExercises = listOf(
    Exercise(
        id = "registration_form",
        title = "Create a Registration Form",
        description = """
            Build a registration form with:
            - Name, Email, Password, Confirm Password fields
            - Radio buttons for gender
            - Dropdown menu for country
            - Checkbox for Terms & Conditions
            - Submit button
            Use <fieldset> and <legend> to group related elements.
        """.trimIndent(),
        starterCode = """
            <!DOCTYPE html>
            <html>
            <body>
              <!-- Your form code here -->
            </body>
            </html>
        """.trimIndent(),
        solutionCode = """
            <!DOCTYPE html>
            <html>
            <body>
              <form>
                <fieldset>
                  <legend>Registration</legend>
                  <label>Name:</label>
                  <input type="text" name="name" required><br><br>
                  <label>Email:</label>
                  <input type="email" name="email" required><br><br>
                  <label>Password:</label>
                  <input type="password" name="password" required><br><br>
                  <label>Confirm Password:</label>
                  <input type="password" name="confirm" required><br><br>
                  <label>Gender:</label>
                  <input type="radio" name="gender" value="male"> Male
                  <input type="radio" name="gender" value="female"> Female<br><br>
                  <label>Country:</label>
                  <select name="country">
                    <option>South Africa</option>
                    <option>India</option>
                    <option>USA</option>
                  </select><br><br>
                  <input type="checkbox" required> I agree to Terms & Conditions<br><br>
                  <button type="submit">Register</button>
                </fieldset>
              </form>
            </body>
            </html>
        """.trimIndent()
    ),
    Exercise(
        id = "quiz_page",
        title = "Create an HTML Quiz Page",
        description = """
            Build a quiz page with:
            - A header and short description
            - At least 3 multiple-choice questions using radio buttons
            - Group each question with <fieldset> and <legend>
            - A submit button
        """.trimIndent(),
        starterCode = """
            <!DOCTYPE html>
            <html>
            <body>
              <!-- Your quiz code here -->
            </body>
            </html>
        """.trimIndent(),
        solutionCode = """
            <!DOCTYPE html>
            <html>
            <body>
              <h1>HTML Basics Quiz</h1>
              <p>Answer the following questions:</p>
              <form>
                <fieldset>
                  <legend>1. What does HTML stand for?</legend>
                  <input type="radio" name="q1"> HyperText Markup Language<br>
                  <input type="radio" name="q1"> HighText Machine Language<br>
                  <input type="radio" name="q1"> Hyperlinks and Text Markup Language<br>
                </fieldset><br>
                <fieldset>
                  <legend>2. Which tag is used for a line break?</legend>
                  <input type="radio" name="q2"> &lt;br&gt;<br>
                  <input type="radio" name="q2"> &lt;break&gt;<br>
                  <input type="radio" name="q2"> &lt;lb&gt;<br>
                </fieldset><br>
                <fieldset>
                  <legend>3. Which tag is used for the largest heading?</legend>
                  <input type="radio" name="q3"> &lt;h1&gt;<br>
                  <input type="radio" name="q3"> &lt;h6&gt;<br>
                  <input type="radio" name="q3"> &lt;head&gt;<br>
                </fieldset><br>
                <button type="submit">Submit Quiz</button>
              </form>
            </body>
            </html>
        """.trimIndent()
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HtmlExercisesScreen() {
    var selected by remember { mutableStateOf<Exercise?>(null) }
    var code by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var showSolution by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("HTML Exercises") }) }) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selected == null) {
                Text("Choose an exercise:", style = MaterialTheme.typography.titleMedium)
                htmlExercises.forEach { ex ->
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
                    hint = "Type your HTML code here..."
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
                    language = "HTML",
                    code = code,
                    diagnostics = diagnosticsText,
                    lessonTitle = selected?.title ?: ""
                    // lessonDescription omitted
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
