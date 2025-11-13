package com.example.codecraft.userinterface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codecraft.auth.AuthViewModel

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    onRegistered: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    val ui = vm.ui.collectAsStateWithLifecycle().value
    val user = vm.user.collectAsStateWithLifecycle().value

    LaunchedEffect(user) {
        if (user != null) onRegistered()
    }

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(ui.error) {
        ui.error?.let { snackbar.showSnackbar(it) }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Create Account", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.email, onValueChange = vm::updateEmail,
                label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ui.password, onValueChange = vm::updatePassword,
                label = { Text("Password (min 6)") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = vm::register,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ui.isLoading) CircularProgressIndicator(Modifier.size(18.dp)) else Text("Register")
            }
            TextButton(onClick = onBackToLogin) { Text("Back to login") }
        }
    }
}