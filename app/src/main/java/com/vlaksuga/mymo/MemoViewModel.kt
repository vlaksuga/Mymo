package com.vlaksuga.mymo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MemoRepository
    var allMemos: LiveData<List<Memo>>
    var viewTypeCode : Int

    init {
        val memosDao = MemoDatabase.getDatabase(application, viewModelScope)!!.memoDao()
        viewTypeCode = 0
        repository = MemoRepository(memosDao)
        allMemos = when(viewTypeCode){
            0 -> repository.allMemos
            1 -> repository.colorOrangeMemos
            else -> repository.allMemos
        }
    }

    fun insert(memo: Memo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(memo)
    }

    fun update(memo: Memo) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(memo)
    }

    fun delete(memo: Memo) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(memo)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

}