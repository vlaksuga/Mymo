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
                    it.dao().groupInsert(group = Group(1, "ETC", "ETC", "#292B2C"))
                    it.dao().groupInsert(group = Group(2, "WORK", "", "#0065A3"))
                    it.dao().groupInsert(group = Group(3, "LIFE STYLE", "", "#008ADF"))
                    it.dao().groupInsert(group = Group(4, "PROJECT", "", "#54E360"))
                    it.dao().groupInsert(group = Group(5, "SIMPLE DIARY", "", "#FFD400"))
                    it.dao().groupInsert(group = Group(6, "SHOPPING", "", "#FF9100"))
                    it.dao().groupInsert(group = Group(7, "IMPORTANT", "", "#FF4949"))
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

