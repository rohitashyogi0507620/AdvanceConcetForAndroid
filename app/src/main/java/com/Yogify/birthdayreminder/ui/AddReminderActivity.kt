package com.Yogify.birthdayreminder.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.databinding.ActivityAddReminderBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_dd_MMMM
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_dd_MMMM_YYYY
import com.Yogify.birthdayreminder.util.utils.Companion.GENDER_FEMALE
import com.Yogify.birthdayreminder.util.utils.Companion.GENDER_MALE
import com.Yogify.birthdayreminder.util.utils.Companion.NOTIFICATION_TYPE_ALL
import com.Yogify.birthdayreminder.util.utils.Companion.NOTIFICATION_TYPE_ONE_MONTH
import com.Yogify.birthdayreminder.util.utils.Companion.NOTIFICATION_TYPE_SAMEDAY
import com.Yogify.birthdayreminder.util.utils.Companion.NOTIFICATION_TYPE_SEVEN_DAY
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDER_TYPE_ANNIVERSARY
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDER_TYPE_BIRTHDAY
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDER_TYPE_OTHER
import com.Yogify.birthdayreminder.util.utils.Companion.cameraStoragePermission
import com.Yogify.birthdayreminder.util.utils.Companion.checkNotificationPermission
import com.Yogify.birthdayreminder.util.utils.Companion.getBitmapFromUri
import com.Yogify.birthdayreminder.util.utils.Companion.getDateToLong
import com.Yogify.birthdayreminder.util.utils.Companion.getLongtoFormate
import com.Yogify.birthdayreminder.util.utils.Companion.showSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


@AndroidEntryPoint
class AddReminderActivity : BaseActivity() {

    lateinit var binding: ActivityAddReminderBinding
    private val mainViewModel: MainViewModel by viewModels()
    var calendar = Calendar.getInstance()
    lateinit var timePicker: MaterialTimePicker
    lateinit var datePicker: MaterialDatePicker<Long>

    lateinit var imgdBitmap: Bitmap
    var reminderType: Int = 0
    var notificationType: Int = 0
    var gender: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intialization()
        onClickListner()

        if (!checkNotificationPermission(applicationContext)) requestNotificationPermission.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )


        // set Max Min Date

//        val today = MaterialDatePicker.todayInUtcMilliseconds()
//        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//
//        calendar.timeInMillis = today
//        calendar[Calendar.MONTH] = Calendar.JANUARY
//        val janThisYear = calendar.timeInMillis
//
//        calendar.timeInMillis = today
//        calendar[Calendar.MONTH] = Calendar.DECEMBER
//        val decThisYear = calendar.timeInMillis
//
//       val constraintsBuilder = CalendarConstraints.Builder().setStart(janThisYear).setEnd(decThisYear)


    }


    val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {

            } else {
            }
        }

    private fun intialization() {
        timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText(getString(R.string.select_time))
            .setHour(calendar.get(Calendar.HOUR_OF_DAY) + 1)
            .setMinute(calendar.get(Calendar.MINUTE)).build()

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//            .setCalendarConstraints(constraintsBuilder.build())
            .setTitleText(getString(R.string.select_date)).build()


    }

    private fun onClickListner() {

        binding.addReminderType.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId.equals(R.id.add_reminder_birthday)) {
                reminderType = REMINDER_TYPE_BIRTHDAY
            } else if (checkedId.equals(R.id.add_reminder_anniversary)) {
                reminderType = REMINDER_TYPE_ANNIVERSARY
            } else if (checkedId.equals(R.id.add_reminder_other)) {
                reminderType = REMINDER_TYPE_OTHER
            }

        }

        binding.addReminderNotificationType.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId.equals(R.id.add_reminder_all)) {
                notificationType = NOTIFICATION_TYPE_ALL
            } else if (checkedId.equals(R.id.add_reminder_sameday)) {
                notificationType = NOTIFICATION_TYPE_SAMEDAY
            } else if (checkedId.equals(R.id.add_reminder_sevenday)) {
                notificationType = NOTIFICATION_TYPE_SEVEN_DAY
            } else if (checkedId.equals(R.id.add_reminder_onemonth)) {
                notificationType = NOTIFICATION_TYPE_ONE_MONTH
            }

        }
        binding.addReminderGender.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == R.id.gender_male) {
                gender = GENDER_MALE
            } else if (checkedId == R.id.gender_female) {
                gender = GENDER_FEMALE
            }
        }

        binding.imgProfile.setOnClickListener {
            cameraStoragePermissionCallback.launch(cameraStoragePermission())
        }

        binding.bntSave.setOnClickListener {
            mainViewModel.insertReminder(
                ReminderItem(
                    0,
                    imgdBitmap,
                    binding.addReminderName.text.toString().trim(),
                    gender,
                    getDateToLong(binding.addReminderDob.text.trim().toString(), DATE_dd_MMMM_YYYY),
                    "12:51",
                    binding.addReminderContact.text.toString().trim(),
                    binding.addReminderWish.text.toString().trim(),
                    getString(R.string.hash) + Integer.toHexString(
                        ContextCompat.getColor(
                            this, R.color.colortheme8
                        ) and 0x00ffffff
                    ),
                    getString(R.string.hash) + Integer.toHexString(
                        ContextCompat.getColor(
                            this, R.color.colortheme8_8
                        ) and 0x00ffffff
                    ),
                    reminderType,
                    true,
                    notificationType,
                    false,
                    false
                )
            )

        }

        binding.productSwitchbtn.setOnClickListener {
        }

        binding.addReminderTime.setOnClickListener {
            timePicker.show(supportFragmentManager, "TIME_PICKER")
        }

        timePicker.addOnPositiveButtonClickListener {
            val newHour: Int = timePicker.hour
            val newMinute: Int = timePicker.minute
            binding.addReminderTime.setText("$newHour:$newMinute")
            binding.addReminderTime.selectAll()
        }

        binding.addReminderDob.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            binding.addReminderDob.setText(getLongtoFormate(it, DATE_dd_MMMM_YYYY))
        }

        binding.addReminderContact.setOnClickListener {
            contactNumberPicker.launch()
//            val i = Intent(Intent.ACTION_PICK)
//            i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
//            startActivityForResult(i, 100)
        }


    }

    val contactNumberPicker =
        registerForActivityResult(ActivityResultContracts.PickContact()) { it ->
            Log.d("DATA", it.toString())
            try {

                val cursor = contentResolver.query(it!!, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val numberIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.PHOTO_ID)
                    val phonenumber = cursor.getString(numberIndex)
                    binding.addReminderContact.setText(phonenumber)
                    cursor.close()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    val cameraStoragePermissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            map.entries.forEach { entry ->
                when (entry.key) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> if (ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showImagePicker()
                    } else {
                        showSnackbar(binding.root, getString(R.string.permission_info))
                    }

                    Manifest.permission.READ_MEDIA_IMAGES -> if (ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.READ_MEDIA_IMAGES
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        showImagePicker()
                    } else {
                        showSnackbar(binding.root, getString(R.string.permission_info))
                    }
                }
            }
        }

    private fun showImagePicker() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    }

    val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imgdBitmap = getBitmapFromUri(applicationContext, uri)
            Glide.with(applicationContext).load(imgdBitmap).diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f).into(binding.imgProfile)
            Log.d("PhotoPicker", "Selected URI: $uri")
        }
    }


}