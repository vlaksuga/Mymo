package com.vlaksuga.mymo

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    // table : memo_table

    @Query("SELECT * from memo_table ORDER BY init_time DESC")
    fun getCurrentIdAll(): LiveData<List<Memo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(memo: Memo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Query("Delete from memo_table")
    suspend fun deleteAll()

    // table : group_table

    @Query("SELECT * from group_table ORDER BY groupId DESC")
    fun getGroupAll(): LiveData<List<Group>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun groupInsert(group: Group)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun groupUpdate(group: Group)

    @Delete
    suspend fun groupDelete(group: Group)

    @Query("Delete from group_table WHERE groupId != :groupId")
    suspend fun deleteAllGroup(groupId : Int)

    // table : trash

    @Query("SELECT * from trash ORDER BY trashExpireTime DESC")
    fun getTrashAll(): LiveData<List<Trash>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun trashInsert(trash : Trash)

    @Delete
    suspend fun trashDelete(trash : Trash)


    @Query("Delete from trash WHERE trashExpireTime < :time")
    suspend fun deleteAllTimeOverTrash(time : Long)

    @Query("Delete from trash")
    suspend fun deleteAllTrash()
}