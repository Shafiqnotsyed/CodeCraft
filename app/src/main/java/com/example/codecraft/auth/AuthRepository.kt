package com.example.codecraft.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?

    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    fun addAuthListener(listener: (FirebaseUser?) -> Unit)
    fun removeAuthListener()
    fun signOut()
}