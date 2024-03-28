package com.Yogify.birthdayreminder.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    // @Inject
//    lateinit var rescheduleAllReminder: RescheduleAllTSDrinkReminders

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val pendingResult = goAsync()
            GlobalScope.launch(Dispatchers.IO) {
                // rescheduleAllReminder()
                pendingResult.finish()
            }
        }
    }
}