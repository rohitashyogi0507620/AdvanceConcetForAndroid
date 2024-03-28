package com.Yogify.birthdayreminder.model

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.Yogify.birthdayreminder.data.db.DataBaseConstant.REMINDER_TABLE
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = REMINDER_TABLE)
@Keep
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var imageUriPath: String,
    var imageWebUriPath: String,
    var name: String,
    var gender: Int,
    var date: Long,
    var mobileNumber: String,
    var wish: String,
    var colorLight: String,
    var colorDark: String,
    var type: Int,
    var isNotify: Boolean,
    var notifyType: Int,
    var isTextMessage: Boolean,
    var isWhatsappMessage: Boolean
) : Parcelable {
    @Ignore
    var imageUri: String = if (imageUriPath.isNullOrEmpty()) imageWebUriPath else imageUriPath

    companion object {
        val Null = ReminderItem(
            id = -1,
            imageUriPath = "",
            imageWebUriPath = "",
            name = "",
            gender = 0,
            date = 0,
            mobileNumber = "",
            wish = "",
            colorLight = "",
            colorDark = "",
            type = 0,
            isNotify = false,
            notifyType = 0,
            isTextMessage = false,
            isWhatsappMessage = false
        )
    }
}
