package com.Yogify.birthdayreminder.data

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.paging.LOGGER
import androidx.sqlite.db.SimpleSQLiteQuery
import com.Yogify.birthdayreminder.db.AppDatabase
import com.Yogify.birthdayreminder.db.DataBaseConstant
import com.Yogify.birthdayreminder.db.DataBaseConstant.REMINDER_DATABASE
import com.Yogify.birthdayreminder.db.DataDAO
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils.Companion.databasePath
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.IOException
import javax.inject.Inject


class MainRepository @Inject constructor(
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
    suspend fun insertReminder(reminderItem: ReminderItem):Long {
        return dataDAO.insertReminder(reminderItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertReminder(reminderItem: List<ReminderItem>):List<Long> {
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