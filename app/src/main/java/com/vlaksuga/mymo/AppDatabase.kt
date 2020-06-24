package com.vlaksuga.mymo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Memo::class, Group::class, Trash::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): Dao

    private class AppDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                scope.launch {
                    // POPULATE GROUP ROW INIT
                    it.dao().groupInsert(group = Group(1, "기타", "분류되지 않은 메모들 입니다.", "#292B2C"))
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: com.vlaksuga.mymo.AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): com.vlaksuga.mymo.AppDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java,
                    "database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

