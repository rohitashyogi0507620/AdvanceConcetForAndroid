package com.Yogify.birthdayreminder.util

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.processing.SurfaceProcessorNode.In
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.data.db.DataBaseConstant.REMINDER_DATABASE
import com.Yogify.birthdayreminder.databinding.AlertDialogBinding
import com.Yogify.birthdayreminder.databinding.BottomsheetOptionmenuBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.model.ThemeColor
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


class utils {

    companion object {

        const val DATE_DD_MMM_YYY = "dd MMM yyyy"
        const val DATE_DD_MMM_YYY_HH_MM = "dd MMMM yyyy , hh:mm"
        const val DATE_dd_MMMM = "dd MMMM"
        const val DATE_MMMM_DD = "MMMM dd"

        const val TIME_HH_MM = "hh:mm"
        const val TIME_HH = "hh"
        const val TIME_MM = "mm"
        const val REMINDER_TYPE_BIRTHDAY = 1
        const val REMINDER_TYPE_ANNIVERSARY = 2
        const val REMINDER_TYPE_OTHER = 3
        const val NOTIFICATION_TYPE_ALL = 1
        const val NOTIFICATION_TYPE_SAMEDAY = 2
        const val NOTIFICATION_TYPE_SEVEN_DAY = 3
        const val NOTIFICATION_TYPE_ONE_MONTH = 4
        const val LAYOUT_GRID = 1
        const val LAYOUT_LINEAR = 2
        const val THEME_LIGHT = 1
        const val THEME_DARK = 2
        const val THEME_AUTO = 0
        const val THEME_DYNAMIC = 3
        const val GENDER_MALE = 1
        const val GENDER_FEMALE = 0
        const val DOWNLOAD_FOLDER = "Download"
        const val PICTURES_FOLDER = "Pictures"
        const val SLASH = "/"
        const val IMAGE_JPEG = ".jpeg"
        const val QR_DATA = "QRDATA"
        const val EDIT_REMINDER = "EDITREMINDER"
        const val REMINDERITEM = "REMINDERITEM"
        const val IS_EDIT_REMINDER = "ISEDITREMINDER"
        const val IMAGE_URL = "IMAGEURL"


        fun longToDate(long: Long, formate: String): String {
            var formatter = SimpleDateFormat(formate, Locale.US)
            return formatter.format(Date(long))
        }

        fun getLongtoFormate(value: Long, formate: String): String {
            val date = Date(value)
            val format = SimpleDateFormat(formate, Locale.US)
            Log.d("DATE", format.format(date))
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


        fun toneWithVibariton(context: Context) {
            toneEffect()
            vibrationEffect(context)
        }

        fun toneEffect() {
            var toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
        }

        fun vibrationEffect(context: Context) {
            var millies: Long = 200
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
                    VibrationEffect.createOneShot(
                        millies, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(millies)
            }
        }

        fun sendTextSMS(phoneNumber: String, message: String) {
            val smsManager: SmsManager = SmsManager.getDefault()
            val parts: ArrayList<String> = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
        }

        fun sendTextWhatsapp(context: Context, phoneNumber: String, message: String) {
            val packageManager: PackageManager = context.getPackageManager()
            val i = Intent(Intent.ACTION_SENDTO)
            try {
                val url =
                    "https://api.whatsapp.com/send?phone=$phoneNumber" + "&text=" + URLEncoder.encode(
                        message, "UTF-8"
                    )
                i.setPackage("com.whatsapp")
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.data = Uri.parse(url)
                if (i.resolveActivity(packageManager) != null) {
                    context.startActivity(i)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun openWhatsApp(context: Context, phoneNumber: String, message: String) {
            try {

                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.type = "text/plain"
                sendIntent.putExtra(Intent.EXTRA_TEXT, message)
                sendIntent.putExtra(
                    "jid", "$phoneNumber@s.whatsapp.net"
                ) //phone number without "+" prefix
                sendIntent.setPackage("com.whatsapp")
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(sendIntent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun CalendarMonth() {
            var calendar = Calendar.getInstance()
            val currentDateDay: Int = calendar.get(Calendar.DAY_OF_MONTH).also {
                Log.d("Calendar", it.toString())
            }
            val currentDateMonth: Int = calendar.get(Calendar.MONTH).also {
                Log.d("Calendar", it.toString())
            }
            val currentDateYear: Int = calendar.get(Calendar.YEAR).also {
                Log.d("Calendar", it.toString())
            }

        }

        fun showAlertDialog(
            context: Context,
            rawAnimationLotiee: Int,
            title: String,
            subtitle: String,
            postiveBtnText: String
        ): Dialog {
            val alertdialog = Dialog(context)
            alertdialog.setContentView(R.layout.alert_dialog)
            alertdialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertdialog.setCanceledOnTouchOutside(false)
            alertdialog.window!!.setLayout(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
            )
            alertdialog.findViewById<TextView?>(R.id.alertSubTitle).text = subtitle
            alertdialog.findViewById<TextView?>(R.id.alertTitle).text = title
            var postiveBtn = alertdialog.findViewById<Button>(R.id.alertBtnPositive)
            postiveBtn.text = postiveBtnText
            alertdialog.findViewById<LottieAnimationView>(R.id.alertLottie)
                .setAnimation(rawAnimationLotiee)
            alertdialog.findViewById<Button>(R.id.alertBtnNegative).setOnClickListener {
                alertdialog.dismiss()
            }
            alertdialog.show()
            return alertdialog
        }

        fun showFullSizeImageDialog(
            context: Context,
            imageurl: String
        ): Dialog {
            val alertdialog = Dialog(context)
            alertdialog.setContentView(R.layout.layout_fullsize_image)
            alertdialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertdialog.setCanceledOnTouchOutside(false)
            alertdialog.window!!.setLayout(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
            )

            Glide.with(context).load(imageurl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).error(R.drawable.ic_profile_demo)
                .into(alertdialog.findViewById<ImageView>(R.id.img_fullsize_profile))

            alertdialog.findViewById<ImageView>(R.id.img_full_size_cancel).setOnClickListener {
                alertdialog.dismiss()
            }
            alertdialog.show()
            return alertdialog
        }


        fun calculateAge(value: Long): Int {

            try {

                val date1 = Calendar.getInstance()
                val date2 = Calendar.getInstance()
                date2.time = Date(value)

                var years = date1.get(Calendar.YEAR) - date2.get(Calendar.YEAR)
                val month = date1.get(Calendar.MONTH) - date2.get(Calendar.MONTH)
                val days = date1.get(Calendar.DAY_OF_MONTH) - date2.get(Calendar.DAY_OF_MONTH)

                Log.d("YEARS", "Year : ${years}  Month : ${month} Days : ${days}")
                if (month < 0) {
                    years = years - 1
                } else if (month == 0) {
                    if (days < 0) {
                        years = years - 1
                    }
                }
                Log.d("YEARS", "Year : ${years}  Month : ${month} Days : ${days}")
                return years
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }


        }

        fun calculateRemainDays(long: Long): Int {

            try {

                val date1 = Calendar.getInstance()
                val date2 = Calendar.getInstance()
                date2.time = Date(long)
                date2.set(Calendar.YEAR, date1.get(Calendar.YEAR))

                var remainmillis: Long = 0
                var upcoming = false
                if (date1.timeInMillis > date2.timeInMillis) {
                    upcoming = true
                    remainmillis = date1.timeInMillis - date2.timeInMillis
                } else {
                    upcoming = false
                    remainmillis = date2.timeInMillis - date1.timeInMillis
                }
                Log.d("YEARS", remainmillis.toString())
                var daysDifference = (remainmillis / (1000 * 60 * 60 * 24)).toInt()
                Log.d("YEARS", daysDifference.toString())
                if (upcoming && daysDifference != 0) daysDifference = 365 - daysDifference
                return daysDifference
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }


        }

        fun remainDaysformate(context: Context, days: Int): String {
            if (days == 0) return context.getString(R.string.today)
            else if (days == 2) return context.getString(R.string.tomorrow)
            else if (days == 7) return context.getString(R.string.oneweekleft)
            else if (days == 14) return context.getString(R.string.twoweekleft)
            else return "${days} " + context.getString(R.string.daysleft)

        }

    }

}