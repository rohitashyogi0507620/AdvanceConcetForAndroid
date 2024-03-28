package com.Yogify.birthdayreminder.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

abstract class BaseBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {}
}
