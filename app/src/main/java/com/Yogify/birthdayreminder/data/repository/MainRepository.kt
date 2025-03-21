package com.Yogify.birthdayreminder.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.Yogify.birthdayreminder.data.db.AppDatabase
import com.Yogify.birthdayreminder.data.db.DataBaseConstant.REMINDER_DATABASE
import com.Yogify.birthdayreminder.data.db.DataDAO
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.ui.notification.NotificationWorker
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_DD_MMM_YYY_HH_MM
import com.Yogify.birthdayreminder.util.utils.Companion.databasePath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class MainRepository @Inject constructor(
    @ApplicationContext var context: Context,
    private val dataDAO: DataDAO, private val appDatabase: AppDatabase
) {

    fun getdao(): DataDAO {
        return dataDAO
    }

    fun getDatabase(): AppDatabase {
        return appDatabase
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertReminder(reminderItem: ReminderItem): Long {
        return dataDAO.insertReminder(reminderItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertReminder(reminderItem: List<ReminderItem>): List<Long> {
        return dataDAO.insertReminder(reminderItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateReminder(reminderItem: ReminderItem): Int {
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

    fun getReminderList(): List<ReminderItem> {
        return dataDAO.getReminderList()
    }

    fun nextReminder(): ReminderItem? {

        var nextreminderItem: ReminderItem? = null

        val currentTimeMillis = Calendar.getInstance(Locale.US).timeInMillis
        var listdata = mutableListOf<ReminderItem>()
        getReminderList().forEachIndexed { index, it ->
            var calendar = Calendar.getInstance(Locale.US)
            calendar.timeInMillis = it.date

            val nextTrigger = Calendar.getInstance().apply {
                timeInMillis = currentTimeMillis
                set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
            }.timeInMillis
            Log.d("TIMENEXT", nextTrigger.toString())
            Log.d("TIMECURRENT", currentTimeMillis.toString())


            if (nextTrigger > currentTimeMillis) {
                listdata.add(it)
            }
        }
        nextreminderItem = listdata.minWithOrNull(Comparator.comparingLong { it.date })
        Log.d("LATESTITEM", nextreminderItem.toString())
        return nextreminderItem

    }

    fun backupDatabase(context: Context): Int {
        var result = -99
        if (instance == null) return result
        val dbFile = context.getDatabasePath(REMINDER_DATABASE)
        val backupFile = File(databasePath(context))
        Log.d("PATH", dbFile.path.toString())
        Log.d("PATH", backupFile.path.toString())
        if (backupFile.exists()) backupFile.delete()
        checkpoint()
        try {
            dbFile.copyTo(backupFile, true)
            result = 0
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    fun restoreDatabase(context: Context, restart: Boolean = true) {
        val backupFile = File(databasePath(context))
        if (backupFile.exists()) {
            if (instance == null) return
            val dbpath = appDatabase.openHelper.readableDatabase.path
            val dbFile = File(dbpath)
            try {
                backupFile.copyTo(dbFile, true)
                checkpoint()
                val i = context.packageManager.getLaunchIntentForPackage(context.packageName)
                i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
                System.exit(0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun checkpoint() {
        var db = appDatabase.openHelper.writableDatabase
        val cursor = db.query("PRAGMA wal_checkpoint(TRUNCATE)", arrayOf())
        cursor.moveToFirst()
    }


}