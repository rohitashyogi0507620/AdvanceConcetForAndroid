package com.Yogify.birthdayreminder.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class utils {

    companion object {

        const val DATE_dd_MMM_yyyy = "dd-MMM-yyyy"
        const val DATE_dd_MMMM = "dd MMMM"
        const val DATE_dd_MMMM_YYYY = "dd MMMM YYYY"
        const val REMINDER_TYPE_BIRTHDAY = 1
        const val REMINDER_TYPE_ANNIVERSARY = 2
        const val REMINDER_TYPE_OTHER = 3
        const val NOTIFICATION_TYPE_ALL = 1
        const val NOTIFICATION_TYPE_SAMEDAY = 2
        const val NOTIFICATION_TYPE_SEVEN_DAY = 3
        const val NOTIFICATION_TYPE_ONE_MONTH = 4
        const val GENDER_MALE = 1
        const val GENDER_FEMALE = 0


        fun getDateToLong(value: String, formate: String): Date {
            var formatter = SimpleDateFormat(formate)
            var date: Date = formatter.parse(value)
            return date
        }


        fun datetoFormate(date: Date, formate: String): String {
            val sdf = SimpleDateFormat(formate)
            return sdf.format(date.time)
        }

        fun getLongtoFormate(value: Long, formate: String): String {
            val date = Date(value)
            val format = SimpleDateFormat(formate)
            return format.format(date)
        }


        private fun getbase65String(context: Context,uri: Uri): String? {
            var bitmap = getBitmapFromUri(context, uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)
        }

        fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
                bitmap = resizeImage(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                bitmap = null
            }
            return bitmap!!
        }

        fun resizeImage(image: Bitmap): Bitmap {

            val width = image.width
            val height = image.height

            val scaleWidth = width / 4
            val scaleHeight = height / 4

            if (image.byteCount <= 1000000) return image

            return Bitmap.createScaledBitmap(image, scaleWidth, scaleHeight, false)
        }
        fun cameraStoragePermission(): Array<String> {
            var permissionStorage: String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionStorage = Manifest.permission.READ_MEDIA_IMAGES
            } else {
                permissionStorage = Manifest.permission.READ_EXTERNAL_STORAGE
            }
            return arrayOf(Manifest.permission.CAMERA, permissionStorage)

        }


        fun showSnackbar(view: View, text: String) {
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        }


        fun checkNotificationPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                } else {
                    return true
                }
            } else {
                return true
            }
        }


    }

}