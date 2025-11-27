package com.example.codecraft

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.codecraft.auth.LoginActivity
import com.example.codecraft.dashboard.DashboardActivity
import com.example.codecraft.data.UserPreferencesRepository
import com.example.codecraft.languageselection.LanguageSelectionActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        userPreferencesRepository = (application as CodeCraftApplication).container.userPreferencesRepository
        val auth = Firebase.auth

        lifecycleScope.launch {
            // First, check if a user is logged in.
            if (auth.currentUser == null) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            } else {
                // If logged in, check if they have completed the initial language selection.
                val isOnboardingComplete = userPreferencesRepository.isOnboardingComplete.first()
                if (isOnboardingComplete) {
                    startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                } else {
                    startActivity(Intent(this@MainActivity, LanguageSelectionActivity::class.java))
                }
            }
            finish()
        }
    }
}
