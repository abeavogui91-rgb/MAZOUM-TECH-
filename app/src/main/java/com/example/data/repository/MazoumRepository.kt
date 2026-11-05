package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.entity.HistoryItemEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class MazoumRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val noteDao = database.noteDao()
    private val taskDao = database.taskDao()
    private val memoryDao = database.memoryDao()
    private val historyDao = database.historyDao()

    private val prefs: SharedPreferences = context.getSharedPreferences("mazoum_prefs", Context.MODE_PRIVATE)

    // Flow getters
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()
    val allHistory: Flow<List<HistoryItemEntity>> = historyDao.getAllHistory()

    // Notes operations
    suspend fun insertNote(title: String, content: String, category: String = "Général") {
        noteDao.insertNote(NoteEntity(title = title, content = content, category = category))
    }

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    suspend fun clearNotes() = noteDao.clearAllNotes()

    // Tasks operations
    suspend fun insertTask(title: String, description: String = "", dueDate: String = "", priority: String = "Normale", category: String = "Personnel") {
        taskDao.insertTask(TaskEntity(title = title, description = description, dueDate = dueDate, priority = priority, category = category))
    }

    suspend fun toggleTaskCompleted(id: Long, completed: Boolean) {
        taskDao.setTaskCompleted(id, completed)
    }

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun clearTasks() = taskDao.clearAllTasks()

    // AI Memory operations
    suspend fun addMemory(category: String, content: String) {
        memoryDao.insertMemory(MemoryEntity(category = category, content = content))
    }

    suspend fun deleteMemory(memory: MemoryEntity) = memoryDao.deleteMemory(memory)
    suspend fun clearMemory() = memoryDao.clearAllMemories()

    // History operations
    suspend fun logHistory(type: String, title: String, preview: String) {
        historyDao.insertHistoryItem(HistoryItemEntity(type = type, title = title, preview = preview))
    }

    suspend fun deleteHistory(id: Long) = historyDao.deleteHistoryById(id)
    suspend fun clearHistory() = historyDao.clearAllHistory()

    // User preferences & profile
    fun getUserName(): String = prefs.getString("user_name", "Explorateur") ?: "Explorateur"
    fun setUserName(name: String) = prefs.edit().putString("user_name", name).apply()

    fun getUserRole(): String = prefs.getString("user_role", "Créateur & Entrepreneur") ?: "Créateur & Entrepreneur"
    fun setUserRole(role: String) = prefs.edit().putString("user_role", role).apply()

    fun isMemoryEnabled(): Boolean = prefs.getBoolean("memory_enabled", true)
    fun setMemoryEnabled(enabled: Boolean) = prefs.edit().putBoolean("memory_enabled", enabled).apply()

    fun isVoiceFeedbackEnabled(): Boolean = prefs.getBoolean("voice_feedback", true)
    fun setVoiceFeedbackEnabled(enabled: Boolean) = prefs.edit().putBoolean("voice_feedback", enabled).apply()

    fun hasCompletedOnboarding(): Boolean = prefs.getBoolean("onboarding_complete", false)
    fun setOnboardingCompleted(completed: Boolean) = prefs.edit().putBoolean("onboarding_complete", completed).apply()

    fun getVoiceSpeed(): Float = prefs.getFloat("voice_speed", 1.0f)
    fun setVoiceSpeed(speed: Float) = prefs.edit().putFloat("voice_speed", speed).apply()

    // Wipe all sensitive data
    suspend fun clearAllData() {
        noteDao.clearAllNotes()
        taskDao.clearAllTasks()
        memoryDao.clearAllMemories()
        historyDao.clearAllHistory()
    }
}
