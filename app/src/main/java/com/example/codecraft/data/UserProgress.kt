package com.example.codecraft.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val userId: String,
    val currentCourse: Course,
    val testScores: Map<String, Int> = emptyMap(), // Changed from completedTests
    val selectedLanguages: Set<String>
)
