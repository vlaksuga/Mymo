package com.vlaksuga.mymo

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MemoDao {
    @Query("SELECT * from memo_table ORDER BY _id DESC")
    fun getCurrentIdAll(): LiveData<List<Memo>>

    @Query("SELECT * from memo_table WHERE bar_color is '#FF8800'")
    fun getColorOrange() : LiveData<List<Memo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(memo: Memo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Query("Delete from memo_table")
    suspend fun deleteAll()
    // TODO 컬러로 필터 만들기
}