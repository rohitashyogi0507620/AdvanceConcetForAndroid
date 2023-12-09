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
    suspend fun insertReminder(reminderItem: ReminderItem) {
        return dataDAO.insertReminder(reminderItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertReminder(reminderItem: List<ReminderItem>) {
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

    fun checkPoint() {
        dataDAO.checkpoint((SimpleSQLiteQuery("pragma wal_checkpoint(full)")))
    }

    fun backupDatabase(context: Context): Int {
        var result = -99
        if (instance == null) return result
        val dbFile = context.getDatabasePath(DataBaseConstant.REMINDER_DATABASE)
        // val bkpFile = File(dbFile.path + DataBaseConstant.DATABASE_BACKUP_SUFFIX)
        val bkpFile =
            File(Environment.getExternalStorageDirectory().path + "/Download/" + "backup_" + REMINDER_DATABASE)
        Log.d("PATH", dbFile.path.toString())
        Log.d("PATH", bkpFile.path.toString())
        if (bkpFile.exists()) bkpFile.delete()
        checkpoint()
        try {
            dbFile.copyTo(bkpFile, true)
            result = 0
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    fun restoreDatabase(context: Context, restart: Boolean = true) {
        if (File(Environment.getExternalStorageDirectory().path + "/Download/" + "backup_" + REMINDER_DATABASE).exists()) {
            if (instance == null) return
            val dbpath = appDatabase.openHelper.readableDatabase.path
            val dbFile = File(dbpath)
            val bkpFile =
                File(Environment.getExternalStorageDirectory().path + "/Download/" + "backup_" + REMINDER_DATABASE)
            try {
                bkpFile.copyTo(dbFile, true)
                checkpoint()
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