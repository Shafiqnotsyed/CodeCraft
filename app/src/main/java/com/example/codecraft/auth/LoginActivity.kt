package com.example.codecraft.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.codecraft.languageselection.LanguageSelectionActivity
import com.example.codecraft.ui.theme.CodeCraftTheme
import com.example.codecraft.userinterface.screens.LoginScreen
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : ComponentActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeCraftTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LoginScreen(
                        onLoginSuccess = {
                            startActivity(Intent(this, LanguageSelectionActivity::class.java))
                            finish()
                        },
                        onRegisterClick = {
                            startActivity(Intent(this, RegisterActivity::class.java))
                        },
                        onForgotPasswordClick = {
                            // Optional: navigate to a dedicated ForgotPasswordActivity if you create one
                             startActivity(Intent(this, ForgotPasswordActivity::class.java))
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let { goToLanguageSelection() }
    }

    private fun signIn(email: String, password: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return
        if (password.length < 6) return

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { goToLanguageSelection() }
            .addOnFailureListener { e ->
                LoginEvents.setError(e.message ?: "Login failed")
            }
    }

    // NEW: Forgot password support
    private fun sendResetEmail(email: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            LoginEvents.setError("Enter a valid email to reset your password.")
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                LoginEvents.setInfo("Password reset email sent to $email")
            }
            .addOnFailureListener { e ->
                LoginEvents.setError(e.message ?: "Could not send password reset email.")
            }
    }

    private fun goToLanguageSelection() {
        val intent = Intent(this, LanguageSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

/* ---------------- Events (add info channel for snackbars) ---------------- */

object LoginEvents {
    private val _error = mutableStateOf<String?>(null)
    private val _info  = mutableStateOf<String?>(null)

    val error: State<String?> get() = _error
    val info:  State<String?> get() = _info

    fun setError(message: String?) { _error.value = message }
    fun setInfo(message: String?)  { _info.value  = message }

    fun clearError() { _error.value = null }
    fun clearInfo()  { _info.value  = null }
}
