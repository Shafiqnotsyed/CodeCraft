package com.example.codecraft.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val ui: StateFlow<AuthUiState> = _ui.asStateFlow()

    private val _user = MutableStateFlow<FirebaseUser?>(repo.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    init {
        // Keep _user in sync with Firebase
        repo.addAuthListener { u -> _user.value = u }
    }

    override fun onCleared() {
        repo.removeAuthListener()
        super.onCleared()
    }

    fun updateEmail(v: String) {
        _ui.value = _ui.value.copy(email = v, error = null)
    }

    fun updatePassword(v: String) {
        _ui.value = _ui.value.copy(password = v, error = null)
    }

    fun clearError() {
        _ui.value = _ui.value.copy(error = null)
    }

    /* ---------------------- Auth Actions ---------------------- */

    fun signIn() = withValidation { email, pass ->
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            val res = repo.signIn(email, pass)   // Result<FirebaseUser>
            _ui.value = _ui.value.copy(isLoading = false)

            res.onSuccess { fbUser ->
                _user.value = fbUser
                _ui.value = _ui.value.copy(error = null)
            }.onFailure { e ->
                _ui.value = _ui.value.copy(error = e.message ?: "Sign-in failed")
            }
        }
    }

    fun register() = withValidation { email, pass ->
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            val res = repo.register(email, pass) // Result<FirebaseUser>
            _ui.value = _ui.value.copy(isLoading = false)

            res.onSuccess { fbUser ->
                _user.value = fbUser
                _ui.value = _ui.value.copy(error = null)
            }.onFailure { e ->
                _ui.value = _ui.value.copy(error = e.message ?: "Registration failed")
            }
        }
    }

    fun sendReset(emailOverride: String? = null) {
        val email = (emailOverride ?: _ui.value.email).trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _ui.value = _ui.value.copy(error = "Enter a valid email")
            return
        }

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            val res = repo.sendPasswordReset(email) // Result<Unit>
            _ui.value = _ui.value.copy(isLoading = false)

            res.onSuccess {
                _ui.value = _ui.value.copy(error = "Reset email sent to $email")
            }.onFailure { e ->
                _ui.value = _ui.value.copy(error = e.message ?: "Failed to send reset email")
            }
        }
    }

    fun signOut() = repo.signOut()

    /* ---------------------- Validation Helper ---------------------- */
    private inline fun withValidation(crossinline block: (String, String) -> Unit) {
        val email = _ui.value.email.trim()
        val pass = _ui.value.password
        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                _ui.value = _ui.value.copy(error = "Enter a valid email")
            pass.length < 6 ->
                _ui.value = _ui.value.copy(error = "Password must be at least 6 characters")
            else -> block(email, pass)
        }
    }
}