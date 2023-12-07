package com.Yogify.birthdayreminder.data

import androidx.annotation.WorkerThread
import com.Yogify.birthdayreminder.db.DataDAO
import com.Yogify.birthdayreminder.model.ReminderItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository @Inject constructor(private val dataDAO: DataDAO) {

    fun getdao(): DataDAO {
        return dataDAO
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertReminder(reminderItem: ReminderItem) {
        return dataDAO.insertReminder(reminderItem)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertReminder(reminderItem:List<ReminderItem>) {
        return dataDAO.insertReminder(reminderItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateReminder(reminderItem: ReminderItem) {
        return dataDAO.updateReminder(reminderItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteReminder(reminderItem: ReminderItem) {
        dataDAO.deleteReminder(reminderItem)
    }


    fun getReminder(): Flow<List<ReminderItem>> {
        return dataDAO.getReminder()
    }

}