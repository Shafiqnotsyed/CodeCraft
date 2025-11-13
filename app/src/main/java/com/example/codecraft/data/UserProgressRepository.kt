package com.example.codecraft.data

import com.example.codecraft.data.db.UserProgressDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * A repository to manage the user's progress, using a local database as the source of truth.
 * This repository is now fully auth-aware.
 */
class UserProgressRepository(private val userProgressDao: UserProgressDao) {

    private val auth = Firebase.auth

    // This flow safely wraps the Firebase auth state listener. It emits the
    // current FirebaseUser when the auth state changes (login/logout).
    private val currentUserFlow: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            // When the auth state changes, send the new user (or null if logged out).
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)

        // When the flow is cancelled, remove the listener to prevent memory leaks.
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * The main userProgress flow. It automatically switches to the correct user's data
     * whenever the authentication state changes.
     */
    val userProgress: Flow<UserProgress?> = currentUserFlow.flatMapLatest { user ->
        if (user != null) {
            // If a user is logged in, query the database for their progress using their UID.
            userProgressDao.getUserProgress(user.uid)
        } else {
            // If no user is logged in, emit null to signify no progress is available.
            flowOf(null)
        }
    }

    /**
     * Inserts or updates the user's progress in the database.
     * This now correctly saves progress against the specific user's record.
     */
    suspend fun updateUserProgress(userProgress: UserProgress) {
        userProgressDao.insertOrUpdateUserProgress(userProgress)
    }
}
