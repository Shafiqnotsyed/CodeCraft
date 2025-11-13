package com.example.codecraft.tests

import com.example.codecraft.data.Question

data class TestsUiState(
    val testCategoryResults: List<TestCategoryResult> = emptyList(),
    val currentTestCategory: String? = null,
    val currentTestQuestions: List<Question> = emptyList(),
    val userAnswers: Map<String, String> = emptyMap(),
    val isTestSubmitted: Boolean = false,
    val lastTestScore: Int = 0,
    val lastTestTotalQuestions: Int = 0,
    val showCompletionScreen: Boolean = false,
    val groupedAndFilteredLanguages: List<LanguageTestGroup> = emptyList()
)

data class TestCategoryResult(
    val category: String,
    val score: Int?,
    val totalQuestions: Int
)

data class LanguageTestGroup(
    val language: String,
    val tests: List<TestCategoryResult>
)
