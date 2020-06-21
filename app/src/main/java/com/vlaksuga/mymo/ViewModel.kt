package com.vlaksuga.mymo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository
    var allMemos: LiveData<List<Memo>>
    var allGroups : LiveData<List<Group>>
    var allGroupNames : LiveData<List<String>>
    var allGroupIds : LiveData<List<Int>>
    var allGroupColors : LiveData<List<String>>

    init {
        val dao = AppDatabase.getDatabase(application, viewModelScope)!!.dao()
        repository = Repository(dao)
        allMemos = repository.allMemos
        allGroups = repository.allGroups
        allGroupNames = repository.allGroupNames
        allGroupIds = repository.allGroupIds
        allGroupColors = repository.allGroupColors
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

    fun groupInsert(group: Group) = viewModelScope.launch(Dispatchers.IO) {
        repository.groupInsert(group)
    }

    fun groupUpdate(group: Group) = viewModelScope.launch(Dispatchers.IO) {
        repository.groupUpdate(group)
    }

    fun groupDelete(group: Group) = viewModelScope.launch(Dispatchers.IO) {
        repository.groupDelete(group)
    }
}