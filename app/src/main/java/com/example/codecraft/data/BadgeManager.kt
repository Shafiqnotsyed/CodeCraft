package com.example.codecraft.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.MilitaryTech

object BadgeManager {
    val allBadges = listOf(
        Badge("Java Novice", "Complete one Java test", Icons.Default.DataObject),
        Badge("Python Padawan", "Complete one Python test", Icons.Default.MilitaryTech),
        Badge("HTML Hero", "Complete one HTML test", Icons.Default.Html),
        Badge("Java Master", "Complete all Java tests", Icons.Default.EmojiEvents),
        Badge("Python Pro", "Complete all Python tests", Icons.Default.EmojiEvents),
        Badge("HTML Guru", "Complete all HTML tests", Icons.Default.EmojiEvents),
    )

    fun getBadgesForCompletedTests(completedTests: Set<String>): List<Badge> {
        val earnedBadges = mutableListOf<Badge>()

        val javaTests = completedTests.filter { it.startsWith("Java", ignoreCase = true) }
        val pythonTests = completedTests.filter { it.startsWith("Python", ignoreCase = true) }
        val htmlTests = completedTests.filter { it.startsWith("HTML", ignoreCase = true) }

        if (javaTests.isNotEmpty()) {
            earnedBadges.add(allBadges.first { it.name == "Java Novice" })
        }
        if (pythonTests.isNotEmpty()) {
            earnedBadges.add(allBadges.first { it.name == "Python Padawan" })
        }
        if (htmlTests.isNotEmpty()) {
            earnedBadges.add(allBadges.first { it.name == "HTML Hero" })
        }

        // Assuming there are 2 tests for each course
        if (javaTests.size >= 2) {
            earnedBadges.add(allBadges.first { it.name == "Java Master" })
        }
        if (pythonTests.size >= 2) {
            earnedBadges.add(allBadges.first { it.name == "Python Pro" })
        }
        if (htmlTests.size >= 2) {
            earnedBadges.add(allBadges.first { it.name == "HTML Guru" })
        }

        return earnedBadges
    }
}
