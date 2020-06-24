package com.vlaksuga.mymo

import androidx.lifecycle.LiveData

class Repository(private val dao: Dao) {
    val allMemos: LiveData<List<Memo>> = dao.getCurrentIdAll()
    val allGroups : LiveData<List<Group>> = dao.getGroupAll()
    val allTrash : LiveData<List<Trash>> = dao.getTrashAll()



    suspend fun insert(memo: Memo) {
        dao.insert(memo)
    }

    suspend fun update(memo: Memo) {
        dao.update(memo)
    }

    suspend fun delete(memo: Memo) {
        dao.delete(memo)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun groupInsert(group: Group) {
        dao.groupInsert(group)
    }

    suspend fun groupUpdate(group: Group) {
        dao.groupUpdate(group)
    }

    suspend fun groupDelete(group: Group) {
        dao.groupDelete(group)
    }

    suspend fun deleteAllGroup(groupId : Int) {
        dao.deleteAllGroup(groupId)
    }

    suspend fun trashInsert(trash: Trash) {
        dao.trashInsert(trash)
    }

    suspend fun trashDelete(trash: Trash) {
        dao.trashDelete(trash)
    }

    suspend fun deleteAllTrash() {
        dao.deleteAllTrash()
    }
}