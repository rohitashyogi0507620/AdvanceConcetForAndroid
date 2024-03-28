package com.Yogify.birthdayreminder.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.Yogify.birthdayreminder.common.ReminderNotificationManager.Companion.ACTION_SMS
import com.Yogify.birthdayreminder.common.ReminderNotificationManager.Companion.ACTION_WHATSAPP
import com.Yogify.birthdayreminder.common.ReminderNotificationManager.Companion.NOTIFICATION_ID
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDERITEM
import com.Yogify.birthdayreminder.util.utils.Companion.openWhatsApp
import com.Yogify.birthdayreminder.util.utils.Companion.sendTextSMS
import com.Yogify.birthdayreminder.util.utils.Companion.sendTextWhatsapp
import com.Yogify.birthdayreminder.util.utils.Companion.stringToReminderDataObject
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActionReceiver : BaseBroadcastReceiver() {

    @Inject
    lateinit var reminderNotificationManager: ReminderNotificationManager

    @Inject
    lateinit var remindMeAlarmManager: ReminderAlarmManager
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == null) return
        var reminderItem = stringToReminderDataObject(intent.getStringExtra(REMINDERITEM)!!)!!
        var notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)

        if (intent.action.equals(ACTION_SMS)) {
            sendTextSMS(reminderItem.mobileNumber, reminderItem.wish)
        } else if (intent.action.equals(ACTION_WHATSAPP)) {
            openWhatsApp(context, reminderItem.mobileNumber, reminderItem.wish)
        }
        reminderNotificationManager.cancelNotification(notificationId)

    }
}