package com.example.codecraft.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CourseRepository {

    private val courses = listOf(
        Course(
            name = "Python",
            icon = "code",
            tests = listOf(
                Test("Python quiz 1", "Python quiz 1"),
                Test("Python quiz 2", "Python quiz 2"),
                Test("Python quiz 3", "Python quiz 3"),
                Test("Python quiz 4", "Python quiz 4"),
                Test("Python quiz 5", "Python quiz 5")
            )
        ),
        Course(
            name = "Java",
            icon = "data_object",
            tests = listOf(
                Test("Java quiz 1", "Java quiz 1"),
                Test("Java quiz 2", "Java quiz 2"),
                Test("Java quiz 3", "Java quiz 3"),
                Test("Java quiz 4", "Java quiz 4"),
                Test("Java quiz 5", "Java quiz 5")
            )
        ),
        Course(
            name = "HTML",
            icon = "html",
            tests = listOf(
                Test("HTML quiz 1", "HTML quiz 1"),
                Test("HTML quiz 2", "HTML quiz 2"),
                Test("HTML quiz 3", "HTML quiz 3"),
                Test("HTML quiz 4", "HTML quiz 4"),
                Test("HTML quiz 5", "HTML quiz 5")
            )
        )
    )

    fun getCourses(): Flow<List<Course>> = flowOf(courses)
}
