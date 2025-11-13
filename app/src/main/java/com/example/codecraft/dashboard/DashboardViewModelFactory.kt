package com.example.codecraft.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.codecraft.data.CourseRepository
import com.example.codecraft.data.UserPreferencesRepository
import com.example.codecraft.data.UserProgressRepository

class DashboardViewModelFactory(
    private val userProgressRepository: UserProgressRepository,
    private val courseRepository: CourseRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(userProgressRepository, courseRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
