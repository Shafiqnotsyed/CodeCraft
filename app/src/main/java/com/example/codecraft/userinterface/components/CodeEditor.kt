package com.example.codecraft.userinterface.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CodeEditor(value: String, onChange: (String) -> Unit, hint: String) {
    TextField(
        value = value,
        onValueChange = onChange,
        label = { Text(hint) },
        modifier = Modifier.fillMaxWidth()
    )
}
