package com.example.codecraft.tests

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.codecraft.CodeCraftApplication
import com.example.codecraft.ui.theme.CodeCraftTheme

class TestsActivity : ComponentActivity() {
    private val viewModel: TestsViewModel by viewModels {
        val application = application as CodeCraftApplication
        TestsViewModelFactory(
            application.container.userProgressRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CodeCraftTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TestsScreen(viewModel)
                }
            }
        }
    }
}
