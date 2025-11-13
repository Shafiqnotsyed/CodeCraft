package com.example.codecraft.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.codecraft.data.BadgeManager
import com.example.codecraft.data.CourseRepository
import com.example.codecraft.data.UserProgressRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    userProgressRepository: UserProgressRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _editableProfile = MutableStateFlow<UserProfile?>(null)
    val editableProfile = _editableProfile.asStateFlow()

    private val firestoreProfileFlow: StateFlow<UserProfile> = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid).snapshots().map { snapshot ->
            snapshot.toObject<UserProfile>() ?: UserProfile(uid = uid, email = auth.currentUser?.email ?: "")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile(uid = uid, email = auth.currentUser?.email ?: ""))
    } ?: MutableStateFlow(UserProfile(email = auth.currentUser?.email ?: "")).asStateFlow()


    val userProfile: StateFlow<UserProfile> = combine(
        firestoreProfileFlow,
        userProgressRepository.userProgress.filterNotNull(),
        courseRepository.getCourses()
    ) { profile, progress, courses ->
        val completedCourses = courses.count { course ->
            course.tests.all { test -> progress.testScores.containsKey(test.id) }
        }
        profile.copy(
            coursesCompleted = completedCourses,
            badges = BadgeManager.getBadgesForCompletedTests(progress.testScores.keys),
            testsTaken = progress.testScores.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UserProfile()
    )

    fun onEditStart() {
        _editableProfile.value = userProfile.value
    }

    fun onEditCancel() {
        _editableProfile.value = null
    }

    fun onNameChange(name: String) {
        _editableProfile.update { it?.copy(name = name) }
    }

    fun onBioChange(bio: String) {
        _editableProfile.update { it?.copy(bio = bio) }
    }

    fun onGenderChange(gender: Gender) {
        val genderString = when (gender) {
            is Gender.Male -> "Male"
            is Gender.Female -> "Female"
        }
        _editableProfile.update { it?.copy(gender = genderString) }
    }

    fun saveProfile() {
        val profileToSave = _editableProfile.value ?: return
        viewModelScope.launch {
            db.collection("users").document(profileToSave.uid).set(profileToSave)
            _editableProfile.value = null
        }
    }
}

class ProfileViewModelFactory(
    private val userProgressRepository: UserProgressRepository,
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userProgressRepository, courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
