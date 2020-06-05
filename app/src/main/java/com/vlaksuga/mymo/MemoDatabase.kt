package com.vlaksuga.mymo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Memo::class], version = 1, exportSchema = false)
abstract class MemoDatabase : RoomDatabase() {

    abstract fun memoDao(): MemoDao

    private class MemoDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                scope.launch {
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MemoDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): MemoDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, MemoDatabase::class.java,
                    "mymo_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(MemoDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

