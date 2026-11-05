package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.HistoryDao
import com.example.data.local.dao.MemoryDao
import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.HistoryItemEntity
import com.example.data.local.entity.MemoryEntity
import com.example.data.local.entity.NoteEntity
import com.example.data.local.entity.TaskEntity

@Database(
    entities = [
        NoteEntity::class,
        TaskEntity::class,
        MemoryEntity::class,
        HistoryItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun memoryDao(): MemoryDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mazoum_tech_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
