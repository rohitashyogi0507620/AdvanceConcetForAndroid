package com.Yogify.birthdayreminder.model

import android.graphics.Bitmap
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.Yogify.birthdayreminder.db.DataBaseConstant.REMINDER_TABLE
import java.util.Date

@Entity(tableName = REMINDER_TABLE)
@Keep
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val bitmap: Bitmap?=null,
    var name: String,
    var gender: Int,
    var date: Date,
    var time: String,
    var mobileNumber: String,
    var wish: String,
    var colorLight: String,
    var colorDark: String,
    var type: Int,
    var isNotify: Boolean,
    var notifyType: Int,
    var isTextMessage: Boolean,
    var isWhatsappMessage: Boolean,
)