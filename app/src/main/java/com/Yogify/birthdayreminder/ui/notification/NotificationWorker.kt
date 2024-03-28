package com.Yogify.birthdayreminder.ui.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.Yogify.birthdayreminder.backup.DriveHelper
import com.Yogify.birthdayreminder.common.ReminderAlarmManager
import com.Yogify.birthdayreminder.data.db.DataDAO
import com.Yogify.birthdayreminder.common.ReminderNotificationManager
import com.Yogify.birthdayreminder.data.repository.MainRepository
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDERITEM
import com.Yogify.birthdayreminder.util.utils.Companion.stringToReminderDataObject
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.annotation.Nullable

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    var reminderAlarmManager: ReminderAlarmManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        reminderAlarmManager.nextReminder()
        return Result.success()
    }
}
