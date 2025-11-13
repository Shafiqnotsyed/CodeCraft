package com.example.codecraft.data

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val name: String,
    val icon: String,
    val tests: List<Test>
)
