package com.example.codecraft.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Html
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconFromName(name: String, modifier: Modifier = Modifier, tint: Color = Color.Unspecified) {
    val icon = when (name) {
        "code" -> Icons.Default.Code
        "data_object" -> Icons.Default.DataObject
        "html" -> Icons.Default.Html
        else -> Icons.Default.Code // Default icon
    }
    Icon(imageVector = icon, contentDescription = null, modifier = modifier, tint = tint)
}
