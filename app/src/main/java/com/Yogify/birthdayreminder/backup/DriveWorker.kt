package com.Yogify.birthdayreminder.backup

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.db.DataDAO
import com.Yogify.birthdayreminder.util.utils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.annotation.Nullable

@HiltWorker
class DriveWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @Nullable var driveHelper: DriveHelper? = null,
    @Nullable var dao: DataDAO? = null
) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val reminderID = inputData.getString(utils.REMINDER_ID)
        val imageUrl = inputData.getString(utils.IMAGE_URL)
        Log.i("MyWorker", "work started")
        Thread.sleep(5000L) // simulate some work
        Log.i("MyWorker", "work finished")

        Result.success()
        if (driveHelper != null && dao != null) {
            imageUrl?.let {
                var folderId = driveHelper?.createFolder(applicationContext.getString(R.string.app_name))
                var driveURL = driveHelper?.uploadImageFile(folderId!!, it)
                dao?.updateImageWrbUrl(reminderID!!, driveURL!!)
            }
        }
        return Result.success()
    }
}
