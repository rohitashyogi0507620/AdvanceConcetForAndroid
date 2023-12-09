package com.Yogify.birthdayreminder.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Yogify.birthdayreminder.data.MainRepository
import com.Yogify.birthdayreminder.db.AppDatabase
import com.Yogify.birthdayreminder.model.ReminderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {


    private val _insertReminder = MutableLiveData<String>()
    val insertReminder = _insertReminder

    private val _deleteReminder = MutableLiveData<String>()
    val deleteReminder = _deleteReminder

    fun getDatabase(): AppDatabase {
        return mainRepository.getDatabase()
    }

    init {

    }


    fun insertReminder(reminderItem: ReminderItem) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.insertReminder(reminderItem).let {
            _insertReminder.postValue(it.toString())
        }
    }

    fun insertReminder(reminderItem: List<ReminderItem>) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.insertReminder(reminderItem).let {
            _insertReminder.postValue(it.toString())
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

    fun restoreDatabase(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.restoreDatabase(context)

    }

}
