package com.Yogify.birthdayreminder

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.Yogify.birthdayreminder.common.ReminderNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {

    @Inject lateinit var reminderNotificationManager: ReminderNotificationManager

    override fun onCreate() {
        super.onCreate()
        //  DynamicColors.applyToActivitiesIfAvailable(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            reminderNotificationManager.createNotificationChannel()
        }
    }

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}