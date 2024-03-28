package com.Yogify.birthdayreminder.common

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.Yogify.birthdayreminder.common.ReminderNotificationManager.Companion.ACTION_NOTIFY
import com.Yogify.birthdayreminder.data.repository.MainRepository
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDERITEM
import java.util.Calendar
import javax.inject.Inject

class ReminderAlarmManager @Inject constructor(
    private val context: Context, var mainRepository: MainRepository
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun getPendingIntent(reminder: ReminderItem): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            reminder.id,
            Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_NOTIFY
                putExtra(REMINDERITEM, utils.reminderDataObjectToString(reminder))
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun isPendingIntentExists(reminder: ReminderItem): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            reminder.id,
            Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_NOTIFY
                putExtra(REMINDERITEM, utils.reminderDataObjectToString(reminder))
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            } else PendingIntent.FLAG_NO_CREATE
        )
    }

    @SuppressLint("ScheduleExactAlarm")
    fun startReminder(reminder: ReminderItem, triggerAt: Long) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            getPendingIntent(reminder)
        )
    }

    fun validateAndStart(reminder: ReminderItem) {

        var calendar = Calendar.getInstance()
        calendar.timeInMillis = reminder.date

        val nextTrigger = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
        }.timeInMillis

        Log.d("Calender", nextTrigger.toString())

        startReminder(reminder, nextTrigger)
    }

    fun nextReminder() {
        var nextReminder = mainRepository.nextReminder()
        if (nextReminder != null) validateAndStart(nextReminder)
    }

    fun cancelReminder(reminder: ReminderItem) {
        alarmManager.cancel(getPendingIntent(reminder))
    }

    fun cancelAllReminder(list: List<ReminderItem>) {
        list.forEach {
            alarmManager.cancel(getPendingIntent(it))
        }
    }

}


