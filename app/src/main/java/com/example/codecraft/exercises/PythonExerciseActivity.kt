package com.example.codecraft.exercises

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.codecraft.ui.theme.CodeCraftTheme
import com.example.codecraft.userinterface.screens.PythonExercisesScreen

class PythonExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeCraftTheme {
                PythonExercisesScreen()
            }
        }
    }
}
