package com.vlaksuga.mymo

import androidx.lifecycle.LiveData

class MemoRepository(private val memoDao: MemoDao) {
    val allMemos: LiveData<List<Memo>> = memoDao.getCurrentIdAll()
    val colorOrangeMemos: LiveData<List<Memo>> = memoDao.getColorOrange()

    suspend fun insert(memo: Memo) {
        memoDao.insert(memo)
    }

    suspend fun update(memo: Memo) {
        memoDao.update(memo)
    }

    suspend fun delete(memo: Memo) {
        memoDao.delete(memo)
    }

    suspend fun deleteAll() {
        memoDao.deleteAll()
    }
}