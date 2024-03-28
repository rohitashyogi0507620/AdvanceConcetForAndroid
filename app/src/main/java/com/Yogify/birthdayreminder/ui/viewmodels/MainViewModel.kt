package com.Yogify.birthdayreminder.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Yogify.birthdayreminder.data.repository.MainRepository
import com.Yogify.birthdayreminder.data.db.AppDatabase
import com.Yogify.birthdayreminder.model.ReminderItem
import com.google.api.services.drive.Drive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.annotation.Nullable
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    @Nullable val drive: Drive?) : ViewModel() {


    private val _insertReminder = MutableLiveData<Long>()
    val insertReminder = _insertReminder
    private val _insertListReminder = MutableLiveData<List<Long>>()
    val insertListReminder = _insertListReminder

    private val _dataRestore = MutableLiveData<Boolean>()
    val dataRestore = _dataRestore

    private val _deleteReminder = MutableLiveData<String>()
    val deleteReminder = _deleteReminder

    fun getDatabase(): AppDatabase {
        return mainRepository.getDatabase()
    }

    init {
        if (drive != null) {
            Log.d("DATA", drive.baseUrl)
        } else {
            Log.d("DATA", "Google Drive Instance Not Found")
        }

    }


    fun insertReminder(reminderItem: ReminderItem) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.insertReminder(reminderItem).let {
            _insertReminder.postValue(it)
        }
    }

    fun updateReminder(reminderItem: ReminderItem) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.updateReminder(reminderItem).let {
            _insertReminder.postValue(it.toLong())
        }
    }

    fun insertReminder(reminderItem: List<ReminderItem>) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.insertReminder(reminderItem).let {
            _insertListReminder.postValue(it)
        }
    }

    fun deleteReminder(reminderItem: ReminderItem) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.deleteReminder(reminderItem).let {
            _deleteReminder.postValue(it.toString())
        }
    }

    fun getReminder(): Flow<List<ReminderItem>> = mainRepository.getReminder()

    fun backupDatabase(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.backupDatabase(context)
    }

    fun restoreDatabase(context: Context) {

        viewModelScope.launch {
            var value = viewModelScope.async {
                mainRepository.restoreDatabase(context)

            }
            value.await()
            _dataRestore.postValue(true)
        }

    }
}
