package com.example.codecraft.languageselection

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Html
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.codecraft.CodeCraftApplication
import com.example.codecraft.data.CourseRepository
import com.example.codecraft.data.UserPreferencesRepository
import com.example.codecraft.data.UserProgress
import com.example.codecraft.data.UserProgressRepository
import com.example.codecraft.dashboard.DashboardActivity
import com.example.codecraft.ui.theme.CodeCraftTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LanguageSelectionActivity : ComponentActivity() {

    private lateinit var userProgressRepository: UserProgressRepository
    private lateinit var courseRepository: CourseRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as CodeCraftApplication).container
        userProgressRepository = container.userProgressRepository
        courseRepository = container.courseRepository
        userPreferencesRepository = container.userPreferencesRepository

        setContent {
            CodeCraftTheme {
                LanguageSelectionScreen { selectedLanguages ->
                    lifecycleScope.launch {
                        saveSelection(selectedLanguages.toSet())
                        navigateToDashboard()
                    }
                }
            }
        }
    }

    private suspend fun saveSelection(selectedLanguages: Set<String>) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val currentProgress = userProgressRepository.userProgress.first()

        val updatedProgress = if (currentProgress != null) {
            // If progress already exists, just update the languages
            currentProgress.copy(selectedLanguages = selectedLanguages)
        } else {
            // If this is the first time, create a new progress object
            val initialCourse = courseRepository.getCourses().first().find { it.name in selectedLanguages }!!
            UserProgress(
                userId = userId,
                currentCourse = initialCourse,
                selectedLanguages = selectedLanguages,
                testScores = emptyMap()
            )
        }
        userProgressRepository.updateUserProgress(updatedProgress)
        userPreferencesRepository.setOnboardingComplete(true)
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun LanguageSelectionScreen(onContinue: (List<String>) -> Unit) {
    var selectedLanguages by remember { mutableStateOf(emptySet<String>()) }
    val languages = listOf(
        "Python" to Icons.Default.Code,
        "Java" to Icons.Default.DataObject,
        "HTML" to Icons.Default.Html
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Choose Your Languages",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(48.dp))

            languages.forEach { (language, icon) ->
                LanguageCard(language = language, icon = icon, isSelected = selectedLanguages.contains(language)) {
                    selectedLanguages = if (selectedLanguages.contains(language)) {
                        selectedLanguages - language
                    } else {
                        selectedLanguages + language
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onContinue(selectedLanguages.toList()) },
                enabled = selectedLanguages.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "Continue", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LanguageCard(language: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val elevation by animateDpAsState(if (isSelected) 8.dp else 2.dp, label = "elevation")
    val border by animateDpAsState(if (isSelected) 2.dp else 0.dp, label = "border")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = border,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = elevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = language, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
