package com.Yogify.birthdayreminder.common

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.ui.activitys.MainActivity
import com.Yogify.birthdayreminder.util.utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.Date
import javax.inject.Inject


class ReminderNotificationManager @Inject constructor(val context: Context) {


    companion object {
        const val ACTION_NOTIFY = "notify_alarm"
        const val ACTION_SMS = "sendSms"
        const val ACTION_WHATSAPP = "sendWhatsapp"
        var CHANNEL_ID = "Reminder"
        var NOTIFICATION_ID = "NOTIFICATION_ID"
    }

    var notifiId = 0

    init {
        notifiId = (Date().getTime() / 1000L % Int.MAX_VALUE).toInt()
    }


    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel() {

        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminder Channel Description"
        }
        notificationManager.createNotificationChannel(channel)

    }

    fun notifyReminder(reminderItem: ReminderItem) {

        Glide.with(context).asBitmap().load(reminderItem.imageUri).error(R.drawable.ic_profile_demo)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE).listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {
                    showNotification(context, reminderItem, null)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    showNotification(context, reminderItem, resource)
                    return true
                }

            }).submit()


    }

    private fun getActionPendingIntent(reminder: ReminderItem, actionvalue: String): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            reminder.id,
            Intent(context, ReminderActionReceiver::class.java).apply {
                action = actionvalue
                putExtra(NOTIFICATION_ID, notifiId)
                putExtra(utils.REMINDERITEM, utils.reminderDataObjectToString(reminder))
            },
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    fun showNotification(
        context: Context, reminderItem: ReminderItem, resource: Bitmap? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(reminderItem.name)
            .setContentText(reminderItem.wish)
            .setColorized(true)
            .setColor(Color.parseColor(reminderItem.colorLight))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (resource != null) {
            notification.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
           // notification.setLargeIcon(resource)
        }

        if (reminderItem.isTextMessage && reminderItem.mobileNumber.isNotEmpty()) {
            notification.addAction(
                0,
                context.getString(R.string.sendSms),
                getActionPendingIntent(reminderItem, ACTION_SMS)
            )
        }
        if (reminderItem.isWhatsappMessage && reminderItem.mobileNumber.isNotEmpty()) {
            notification.addAction(
                0,
                context.getString(R.string.sendWhatsapp),
                getActionPendingIntent(reminderItem, ACTION_WHATSAPP)
            )
        }


        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notifiId, notification.build())
            }
        }
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

}