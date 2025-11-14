package com.example.codecraft.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages", indices = [Index(value = ["userId"])])
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
