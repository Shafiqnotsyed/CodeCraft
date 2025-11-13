package com.example.codecraft.profile

import com.example.codecraft.data.Badge
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

sealed class Gender {
    data object Male : Gender()
    data object Female : Gender()
}

// This data class represents the structure of our document in Firestore.
// The UI-specific or calculated properties are marked with @Exclude.
data class UserProfile(
    @DocumentId val uid: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val gender: String = "Female", // Stored as a simple String ("Male" or "Female")
    @ServerTimestamp val lastModified: Date? = null,

    // These fields are populated at runtime by the ViewModel and are not stored in Firestore.
    @get:Exclude var badges: List<Badge> = emptyList(),
    @get:Exclude var coursesCompleted: Int = 0,
    @get:Exclude var testsTaken: Int = 0
) {
    // This computed property converts the String from Firestore into the sealed class the UI uses.
    @get:Exclude
    val genderEnum: Gender
        get() = when (gender) {
            "Male" -> Gender.Male
            else -> Gender.Female
        }
}
