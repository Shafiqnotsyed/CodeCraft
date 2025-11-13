package com.example.codecraft.data

import android.content.Context
import com.example.codecraft.data.ai.AiProvider
import com.example.codecraft.data.ai.GeminiAiProvider
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.db.UserProgressDao

/**
 * A simple container for dependency injection.
 */
interface AppContainer {
    val appDatabase: AppDatabase
    val userProgressDao: UserProgressDao
    val userProgressRepository: UserProgressRepository
    val courseRepository: CourseRepository
    val userPreferencesRepository: UserPreferencesRepository
    val aiProvider: AiProvider
}

/**
 * The default implementation of [AppContainer].
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val userProgressDao: UserProgressDao by lazy {
        appDatabase.userProgressDao()
    }

    override val userProgressRepository: UserProgressRepository by lazy {
        UserProgressRepository(userProgressDao)
    }

    override val courseRepository: CourseRepository by lazy {
        CourseRepository()
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val aiProvider: AiProvider by lazy {
        GeminiAiProvider()
    }
}
