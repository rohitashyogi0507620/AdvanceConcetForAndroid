package com.Yogify.birthdayreminder.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {


    private val _reminderlist = MutableLiveData<List<ReminderItem>>()
    val reminderlist = _reminderlist

    private val _insertReminder = MutableLiveData<String>()
    val insertReminder = _insertReminder

    private val _deleteReminder = MutableLiveData<String>()
    val deleteReminder = _deleteReminder

    val reminderList = Pager(
        PagingConfig(
            pageSize = 5,
            enablePlaceholders = true,
            initialLoadSize = 5
        ),
    ) {
        MainPagingSource(dao = mainRepository.getdao())
    }.flow.cachedIn(viewModelScope)

    init {



        //getReminder()
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

   // fun getReminder(): Flow<List<ReminderItem>> = mainRepository.getReminder()

}
