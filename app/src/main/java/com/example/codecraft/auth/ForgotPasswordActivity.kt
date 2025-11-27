package com.example.codecraft.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.codecraft.ui.theme.CodeCraftTheme
import com.example.codecraft.userinterface.screens.ForgotPasswordScreen

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeCraftTheme {
                ForgotPasswordScreen(
                    onBack = { finish() }  // returns to LoginActivity
                )
            }
        }
    }
}
