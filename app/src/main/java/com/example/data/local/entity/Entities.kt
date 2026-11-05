package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Général",
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: String = "",
    val priority: String = "Normale", // "Basse", "Normale", "Haute", "Urgente"
    val isCompleted: Boolean = false,
    val category: String = "Personnel",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_memory")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String = "Préférence", // "Identité", "Préférence", "Objectif", "Projet", "Fait"
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history_items")
data class HistoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "CHAT", "VISION", "STUDIO", "VOICE"
    val title: String,
    val preview: String,
    val timestamp: Long = System.currentTimeMillis()
)
