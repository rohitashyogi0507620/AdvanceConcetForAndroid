package com.Yogify.birthdayreminder

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        //  DynamicColors.applyToActivitiesIfAvailable(this)

    }

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() =Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}