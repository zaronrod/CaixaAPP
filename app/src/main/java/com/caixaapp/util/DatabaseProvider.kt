package com.caixaapp.util

import android.content.Context
import androidx.room.Room
import com.caixaapp.repository.AppDatabase

object DatabaseProvider {
    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "caixaapp.db"
            ).build().also { instance = it }
        }
    }
}
