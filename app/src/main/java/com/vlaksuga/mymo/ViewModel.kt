package com.vlaksuga.mymo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class ViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository
    var allMemos: LiveData<List<Memo>>
    var allGroups : LiveData<List<Group>>
    var allTrash : LiveData<List<Trash>>

    init {
        val dao = AppDatabase.getDatabase(application, viewModelScope)!!.dao()
        repository = Repository(dao)
        allMemos = repository.allMemos
        allGroups = repository.allGroups
        allTrash = repository.allTrash
    }

    fun insert(memo: Memo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(memo)
    }

    fun undoMemo(memo: Memo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(memo)
        val expireDateAfter = 15
        val trashId = memo._id
        val trashTitle = memo.memoTitle
        val trashContent = memo.memoContent
        val trashInitTime = Date().time
        val trashExpireTime = trashInitTime + (86400000*expireDateAfter)
        repository.trashDelete(trash = Trash(trashId, trashTitle, trashContent, trashInitTime, trashExpireTime))
    }

    fun update(memo: Memo) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(memo)
    }

    fun delete(memo: Memo) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(memo)
        val expireDateAfter = 15
        val oneDay = 86400000
        val trashId = memo._id
        val trashTitle = memo.memoTitle
        val trashContent = memo.memoContent
        val trashInitTime = Date().time
        val trashExpireTime = trashInitTime + (oneDay*expireDateAfter)
        repository.trashInsert(trash = Trash(trashId, trashTitle, trashContent, trashInitTime, trashExpireTime))
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

    fun deleteAllGroup(groupId : Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllGroup(groupId)
    }

    fun trashDelete(trash: Trash) = viewModelScope.launch(Dispatchers.IO) {
        repository.trashDelete(trash)
    }

    fun trashInsert(trash: Trash) = viewModelScope.launch(Dispatchers.IO) {
        repository.trashInsert(trash)
    }

    fun recoverTrash(trash: Trash) = viewModelScope.launch(Dispatchers.IO) {
        repository.trashDelete(trash)
        val memoId = trash.trashId
        val memoTitle = trash.trashTitle
        val memoContent = trash.trashContent
        val initTime = Date().time
        val isImportant = false
        val memoGroupId = 1
        val memoGroupColor = "#292B2C"
        val memoGroupName = "ETC"
        repository.insert(memo = Memo(memoId, memoTitle, memoContent, initTime, isImportant, memoGroupId, memoGroupColor, memoGroupName))
    }

    fun undoTrash(trash: Trash) = viewModelScope.launch(Dispatchers.IO) {
        repository.trashInsert(trash)
        val memoId = trash.trashId
        val memoTitle = trash.trashTitle
        val memoContent = trash.trashContent
        val initTime = Date().time
        val isImportant = false
        val memoGroupId = 1
        val memoGroupColor = "#292B2C"
        val memoGroupName = "ETC"
        repository.delete(memo = Memo(memoId, memoTitle, memoContent, initTime, isImportant, memoGroupId, memoGroupColor, memoGroupName))
    }

    fun deleteAllTimeOverTrash(time : Long) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllTimeOverTrash(time)
    }

    fun deleteAllTrash() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllTrash()
    }
}