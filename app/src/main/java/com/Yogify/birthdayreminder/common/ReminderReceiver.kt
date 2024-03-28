package com.Yogify.birthdayreminder.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.Yogify.birthdayreminder.data.repository.MainRepository
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDERITEM
import com.Yogify.birthdayreminder.util.utils.Companion.stringToReminderDataObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BaseBroadcastReceiver() {

    @Inject
    lateinit var reminderNotificationManager: ReminderNotificationManager
    @Inject
    lateinit var remindMeAlarmManager: ReminderAlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == null) return
        if (intent.action == ReminderNotificationManager.ACTION_NOTIFY) {
            reminderNotificationManager.notifyReminder(stringToReminderDataObject(intent.getStringExtra(REMINDERITEM)!!)!!)
            remindMeAlarmManager.nextReminder()
        }
    }
}