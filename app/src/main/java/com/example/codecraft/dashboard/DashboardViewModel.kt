package com.example.codecraft.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codecraft.data.Course
import com.example.codecraft.data.CourseRepository
import com.example.codecraft.data.UserPreferencesRepository
import com.example.codecraft.data.UserProgress
import com.example.codecraft.data.UserProgressRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userProgressRepository: UserProgressRepository,
    private val courseRepository: CourseRepository,
    private val userPreferencesRepository: UserPreferencesRepository // Keep for onboarding
) : ViewModel() {

    private val auth = Firebase.auth

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val userProgress: StateFlow<UserProgress?> = userProgressRepository.userProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    val courses: StateFlow<List<Course>> = combine(
        courseRepository.getCourses(),
        userProgress // Now depends on the user's actual progress
    ) { allCourses, progress ->
        val selectedLangs = progress?.selectedLanguages ?: emptySet()
        allCourses.filter { it.name in selectedLangs }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    val learningProgress: StateFlow<Float> = combine(
        userProgress,
        courses
    ) { progress, courseList ->
        if (progress == null || courseList.isEmpty()) return@combine 0f
        val course = courseList.find { it.name == progress.currentCourse.name }
        if (course == null || course.tests.isEmpty()) return@combine 0f

        val completed = progress.testScores.keys.count { testId -> course.tests.any { it.id == testId } }
        completed.toFloat() / course.tests.size.toFloat()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = 0f
    )

    fun onCourseSelected(course: Course) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val currentProgress = userProgress.value
            if (currentProgress != null) {
                val newProgress = currentProgress.copy(currentCourse = course)
                userProgressRepository.updateUserProgress(newProgress)
            }
        }
    }

    init {
        viewModelScope.launch {
            try {
                // Wait for the user progress to be loaded before finishing initialization.
                userProgress.first { it != null || auth.currentUser == null }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
