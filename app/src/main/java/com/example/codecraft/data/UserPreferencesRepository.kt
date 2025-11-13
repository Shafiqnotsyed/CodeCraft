package com.example.codecraft.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * This repository is now responsible for device-specific preferences,
 * like whether the user has completed the initial setup.
 */
class UserPreferencesRepository(private val context: Context) {

    private val onboardingCompleteKey = booleanPreferencesKey("onboarding_complete")

    /**
     * A flow that emits true if the user has completed the language selection onboarding.
     */
    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[onboardingCompleteKey] ?: false
        }

    /**
     * Marks the onboarding process as complete.
     */
    suspend fun setOnboardingComplete(isComplete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[onboardingCompleteKey] = isComplete
        }
    }
}
