package com.Yogify.birthdayreminder.util

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.db.DataBaseConstant.REMINDER_DATABASE
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.model.ThemeColor
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class utils {

    companion object {

        const val DATE_DD_MMM_YYY = "dd MMM yyyy"
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
        const val DOWNLOAD_FOLDER = "Download"
        const val PICTURES_FOLDER = "Pictures"
        const val SLASH = "/"
        const val IMAGE_JPEG = ".jpeg"
        const val QR_DATA = "QRDATA"
        const val IMAGE_URL = "IMAGEURL"
        const val REMINDER_ID = "REMINDERID"


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

        fun getDateToString(date: Date,formate: String):String{
            val format = SimpleDateFormat(formate)
            return format.format(date)
        }


        fun getbase65String(bitmap: Bitmap): String? {
            try {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)

            } catch (e: Exception) {
                return null
            }

        }

        fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
                bitmap = resizeImage(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                bitmap = null
            }
            return bitmap
        }

        fun resizeImage(image: Bitmap): Bitmap {

            val width = image.width
            val height = image.height

            val scaleWidth = width / 2
            val scaleHeight = height / 2

            if (image.byteCount <= 100000) return image

            return Bitmap.createScaledBitmap(image, scaleWidth, scaleHeight, false)
        }

        fun cameraStoragePermission(): Array<String> {
            var permissionStorage: String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionStorage = Manifest.permission.READ_MEDIA_IMAGES
            } else {
                permissionStorage = Manifest.permission.READ_EXTERNAL_STORAGE
            }
            return arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                permissionStorage
            )

        }


        fun showSnackbar(view: View, text: String) {
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        }

        fun colorTheme(context: Context): ArrayList<ThemeColor> {

            var themeColor = arrayListOf<ThemeColor>()
            var colorLight = context.getResources().getStringArray(R.array.colorThemeLight)
            var colorDark = context.getResources().getStringArray(R.array.colorThemeDark)

            colorLight.forEachIndexed { index, s ->
                themeColor.add(ThemeColor(colorLight.get(index), colorDark.get(index)))
            }
            return themeColor
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

        fun saveImageintoStorage(bmp: Bitmap, name: String, path: String): String? {

            try {
                var fileName = name.replace(" ", "")
                val parentFile = File(path)
                if (!parentFile.exists()) {
                    parentFile.mkdirs()
                }
                val imageFile = File(parentFile, fileName)
                var out: FileOutputStream? = null
                out = FileOutputStream(imageFile)
                bmp.compress(Bitmap.CompressFormat.JPEG, 30, out)
                out.close()
                Log.d("PATH", imageFile.absolutePath)
                return imageFile.absolutePath
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun appfolderPath(context: Context): String {
            return Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + context.getString(
                R.string.app_name
            )
        }

        fun imageFolderPath(context: Context): String {
            return appfolderPath(context) + File.separator + PICTURES_FOLDER
        }

        fun databasePath(context: Context): String {
            return appfolderPath(context) + File.separator + REMINDER_DATABASE

        }

        fun showQrcode(text: String): Bitmap? {
            var mWriter = MultiFormatWriter();
            try {
                var mMatrix = mWriter.encode(text, BarcodeFormat.QR_CODE, 600, 600);
                var mEncoder = BarcodeEncoder()
                return mEncoder.createBitmap(mMatrix)
            } catch (e: Exception) {
                return null
            }
        }

        fun stringToReminderDataObject(data: String): ReminderItem? {
            try {
                var reminderItem: ReminderItem = Gson().fromJson(
                    data, object : TypeToken<ReminderItem>() {}.type
                )
                return reminderItem
            } catch (e: Exception) {
                return null
            }
        }

        fun reminderDataObjectToString(item: ReminderItem): String {
            return Gson().toJson(item)
        }
    }

}