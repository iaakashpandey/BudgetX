package com.example.budgetx.Database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: TransactionDatabase? = null

    fun getDatabase(context: Context): TransactionDatabase {
        return INSTANCE ?: synchronized(this) { // Use synchronized block for thread safety
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TransactionDatabase::class.java,
                "transaction_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}

