package com.example.codecraft.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.codecraft.ui.theme.CodeCraftTheme
import com.example.codecraft.userinterface.screens.ForgotPasswordScreen

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeCraftTheme {
                ForgotPasswordScreen(
                    onBack = { finish() }  // returns to LoginActivity
                )
            }
        }
    }
}