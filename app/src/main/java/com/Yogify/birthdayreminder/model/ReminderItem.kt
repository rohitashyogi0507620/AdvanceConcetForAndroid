package com.Yogify.birthdayreminder.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.Yogify.birthdayreminder.db.DataBaseConstant.REMINDER_TABLE
import java.util.Date

@Entity(tableName = REMINDER_TABLE)
@Keep
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var imageUrl: String,
    var name: String,
    var gender: String,
    var date: Date,
    var time: String,
    var mobileNumber: String,
    var wish: String,
    var colorLight: String,
    var colorDark: String,
    var type: String,
    var isNotify: Boolean,
    var notifyType: String,
    var isTextMessage: Boolean,
    var isWhatsappMessage: Boolean,
)