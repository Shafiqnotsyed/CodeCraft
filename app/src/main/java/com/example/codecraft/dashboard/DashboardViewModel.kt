package com.example.codecraft.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codecraft.data.Course
import com.example.codecraft.data.CourseRepository
import com.example.codecraft.data.UserPreferencesRepository
import com.example.codecraft.data.UserProgressRepository
import com.example.codecraft.profile.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userProgressRepository: UserProgressRepository,
    private val courseRepository: CourseRepository,
    userPreferencesRepository: UserPreferencesRepository // Keep for onboarding
) : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val firestoreProfileFlow: StateFlow<UserProfile> = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid).snapshots().map { snapshot ->
            snapshot.toObject<UserProfile>() ?: UserProfile(uid = uid, email = auth.currentUser?.email ?: "")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile(uid = uid, email = auth.currentUser?.email ?: ""))
    } ?: MutableStateFlow(UserProfile(email = auth.currentUser?.email ?: ""))

    val uiState: StateFlow<DashboardUiState> = combine(
        userProgressRepository.userProgress,
        courseRepository.getCourses(),
        firestoreProfileFlow
    ) { userProgress, allCourses, userProfile ->
        val selectedLangs = userProgress?.selectedLanguages ?: emptySet()
        val courses = allCourses.filter { it.name in selectedLangs }

        val learningProgress = if (userProgress == null || courses.isEmpty()) 0f
        else {
            val course = courses.find { it.name == userProgress.currentCourse.name }
            if (course == null || course.tests.isEmpty()) 0f
            else {
                val completed = userProgress.testScores.keys.count { testId -> course.tests.any { it.id == testId } }
                completed.toFloat() / course.tests.size.toFloat()
            }
        }

        DashboardUiState(
            isLoading = userProgress == null,
            userProgress = userProgress,
            courses = courses,
            learningProgress = learningProgress,
            userName = userProfile.name
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = DashboardUiState()
    )

    fun onCourseSelected(course: Course) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val currentProgress = uiState.value.userProgress
            if (currentProgress != null) {
                val newProgress = currentProgress.copy(currentCourse = course)
                userProgressRepository.updateUserProgress(newProgress)
            }
        }
    }

    init {
        viewModelScope.launch {
            // The combine operator will handle the loading state, so we just need to make sure
            // the user progress is being collected.
            userProgressRepository.userProgress.first { it != null || auth.currentUser == null }
        }
    }
}
