package com.example.codecraft.dashboard

import com.example.codecraft.data.Course
import com.example.codecraft.data.UserProgress

data class DashboardUiState(
    val isLoading: Boolean = true,
    val userProgress: UserProgress? = null,
    val courses: List<Course> = emptyList(),
    val learningProgress: Float = 0f,
    val userName: String = ""
)
