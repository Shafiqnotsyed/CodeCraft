package com.example.codecraft.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.codecraft.CodeCraftApplication
import com.example.codecraft.auth.LoginActivity
import com.example.codecraft.chat.AiChatActivity
import com.example.codecraft.exercises.HtmlExerciseActivity
import com.example.codecraft.exercises.JavaExerciseActivity
import com.example.codecraft.exercises.PythonExerciseActivity
import com.example.codecraft.languageselection.LanguageSelectionActivity
import com.example.codecraft.profile.ProfileActivity
import com.example.codecraft.tests.TestsActivity
import com.example.codecraft.ui.theme.CodeCraftTheme
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels {
        val application = application as CodeCraftApplication
        val container = application.container
        DashboardViewModelFactory(
            container.userProgressRepository,
            container.courseRepository,
            container.userPreferencesRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CodeCraftTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val uiState by viewModel.uiState.collectAsState()

                    if (uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.userProgress != null) {
                        DashboardScreen(
                            uiState = uiState,
                            onCourseSelected = { course -> viewModel.onCourseSelected(course) },
                            onSignOut = { signOut() },
                            onChangeCourses = { changeCourses() },
                            onAllTests = { startActivity(Intent(this, TestsActivity::class.java)) },
                            onMyProfile = { startActivity(Intent(this, ProfileActivity::class.java)) },
                            onAskAi = { startActivity(Intent(this, AiChatActivity::class.java)) },
                            onPythonExercise = { startActivity(Intent(this, PythonExerciseActivity::class.java)) },
                            onJavaExercise = { startActivity(Intent(this, JavaExerciseActivity::class.java)) },
                            onHtmlExercise = { startActivity(Intent(this, HtmlExerciseActivity::class.java)) }
                        )
                    } else {
                        // This screen is now only shown if there is genuinely no user progress record.
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No courses selected. Please go back and select at least one course to continue.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { changeCourses() }) {
                                    Text("Select Courses")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeCourses() {
        val intent = Intent(this, LanguageSelectionActivity::class.java)
        startActivity(intent)
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
