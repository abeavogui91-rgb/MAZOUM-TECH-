package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.HistoryItemEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM ai_memory ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("DELETE FROM ai_memory WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("DELETE FROM ai_memory")
    suspend fun clearAllMemories()
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_items ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: HistoryItemEntity): Long

    @Query("DELETE FROM history_items WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM history_items")
    suspend fun clearAllHistory()
}
