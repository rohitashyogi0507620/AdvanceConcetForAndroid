package com.Yogify.birthdayreminder.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.backup.DriveHelper
import com.Yogify.birthdayreminder.backup.DriveWorker
import com.Yogify.birthdayreminder.databinding.ActivityAddReminderBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.model.ThemeColor
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_DD_MMM_YYY
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
import com.Yogify.birthdayreminder.util.utils.Companion.colorTheme
import com.Yogify.birthdayreminder.util.utils.Companion.getBitmapFromUri
import com.Yogify.birthdayreminder.util.utils.Companion.getDateToLong
import com.Yogify.birthdayreminder.util.utils.Companion.getDateToString
import com.Yogify.birthdayreminder.util.utils.Companion.getLongtoFormate
import com.Yogify.birthdayreminder.util.utils.Companion.imageFolderPath
import com.Yogify.birthdayreminder.util.utils.Companion.saveImageintoStorage
import com.Yogify.birthdayreminder.util.utils.Companion.showSnackbar
import com.Yogify.birthdayreminder.util.utils.Companion.stringToReminderDataObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.annotation.Nullable
import javax.inject.Inject


@AndroidEntryPoint
class AddReminderActivity : BaseActivity(),
    androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    lateinit var binding: ActivityAddReminderBinding
    private val mainViewModel: MainViewModel by viewModels()
    var calendar = Calendar.getInstance()
    lateinit var timePicker: MaterialTimePicker
    lateinit var datePicker: MaterialDatePicker<Long>

    var imageUri: String? = null
    var reminderType: Int = 0
    var notificationType: Int = 0
    var gender: Int = 1
    val colorAdpter = ColorAdpter()
    var themeColor: ThemeColor? = null


    @set:Inject
    @Nullable
    var driveHelper: DriveHelper? = null

    private val _imgBitmap = MutableLiveData<Bitmap>()
    val imgBitmap = _imgBitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intialization()
        onClickListner()
        observer()

        if (!checkNotificationPermission(applicationContext)) requestNotificationPermission.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )


        binding.addReminderColorList.adapter = colorAdpter
        colorAdpter.submitList(colorTheme(applicationContext))


//        val periodicWorkRequest = PeriodicWorkRequestBuilder<DriveWorkManager>(24, TimeUnit.HOURS).build()
//        WorkManager.getInstance().enqueue(periodicWorkRequest)

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

    private fun observer() {
        mainViewModel.insertReminder.observe(this, Observer {

            val inputData = Data.Builder().putString(utils.IMAGE_URL, imageUri)
                .putString(utils.REMINDER_ID, it.toString()).build()
            val uploadDataConstraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val uploadWorkRequest = OneTimeWorkRequestBuilder<DriveWorker>().setInputData(inputData)
                .setConstraints(uploadDataConstraints).build()
            WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)
            showSnackbar(binding.root, getString(R.string.reminderaddedsuccessfully))
            finishAfterTransition()

        })

        colorAdpter.themeColor.observe(this, Observer {
            themeColor = it
            binding.scrollBar.setBackgroundColor(Color.parseColor(themeColor?.colorLight))
            binding.appBarLayout.setBackgroundColor(Color.parseColor(themeColor?.colorDark))
            window.statusBarColor = Color.parseColor(themeColor?.colorDark)
            binding.imgProfile.strokeColor =
                ColorStateList.valueOf(Color.parseColor(themeColor?.colorDark))
            binding.addReminderSubmit.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(themeColor?.colorDark))
            binding.addReminderSubmit.setTextColor(Color.parseColor(themeColor?.colorLight))

//            val animationUtils = ViewAnimationUtils.createCircularReveal(
//                binding.scrollBar,
//                binding.scrollBar.getWidth(),
//                100,
//                20f,
//                binding.scrollBar.getHeight().toFloat()
//            )
//            animationUtils.duration = 1000
//            animationUtils.addListener(object : Animator.AnimatorListener {
//                override fun onAnimationStart(animation: Animator) {
//                    binding.scrollBar.setBackgroundColor(Color.parseColor(themeColor?.colorLight))
//                }
//
//                override fun onAnimationEnd(animation: Animator) {
//                    binding.scrollBar.setBackgroundColor(Color.parseColor(themeColor?.colorLight))
//                    binding.appBarLayout.setBackgroundColor(Color.parseColor(themeColor?.colorDark))
//                    window.statusBarColor = Color.parseColor(themeColor?.colorDark)
//                    binding.imgProfile.strokeColor = ColorStateList.valueOf(Color.parseColor(themeColor?.colorDark))
//                    binding.addReminderSubmit.backgroundTintList = ColorStateList.valueOf(Color.parseColor(themeColor?.colorDark))
//                    binding.addReminderSubmit.setTextColor(Color.parseColor(themeColor?.colorLight))
//
//                }
//
//                override fun onAnimationCancel(animation: Animator) {}
//                override fun onAnimationRepeat(animation: Animator) {}
//            })
//            animationUtils.start()

        })
        imgBitmap.observe(this, Observer {
            Glide.with(applicationContext).load(it)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.imgProfile)
        })

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
        binding.searchBar.setOnMenuItemClickListener(this)


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
        binding.addReminderSubmit.setOnClickListener {
            binding.progressbar.visibility = View.VISIBLE

            imageUri = saveImageintoStorage(
                imgBitmap.value!!,
                binding.addReminderName.text.toString().trim()+getDateToLong(binding.addReminderDob.text.trim().toString(), DATE_DD_MMM_YYY).time.toString(),
                imageFolderPath(applicationContext)
            )

            mainViewModel.insertReminder(
                ReminderItem(
                    0,
                    imageUri.toString(),
                    "",
                    binding.addReminderName.text.toString().trim(),
                    gender,
                    getDateToLong(binding.addReminderDob.text.trim().toString(), DATE_DD_MMM_YYY),
                    binding.addReminderTime.text.toString(),
                    binding.addReminderContact.text.toString().trim(),
                    binding.addReminderWish.text.toString().trim(),
                    themeColor?.colorLight ?: "#FAEDD0",
                    themeColor?.colorDark ?: "#CCA54F",
                    reminderType,
                    true,
                    notificationType,
                    binding.addReminderTextmessage.isChecked,
                    binding.addReminderWhatsappmessage.isChecked
                )
            )

        }

        binding.addReminderWhatsappmessage.setOnCheckedChangeListener { compoundButton, ischecked ->

        }
        binding.addReminderTextmessage.setOnCheckedChangeListener { compoundButton, ischecked ->

        }


        binding.addReminderTime.setOnClickListener {
            timePicker.show(supportFragmentManager, "TIME_PICKER")
        }

        timePicker.addOnPositiveButtonClickListener {
            val newHour: Int = timePicker.hour
            val newMinute: Int = timePicker.minute
            binding.addReminderTime.setText("$newHour:$newMinute")

        }

        binding.addReminderDob.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            binding.addReminderDob.setText(getLongtoFormate(it, DATE_DD_MMM_YYY))
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
            _imgBitmap.postValue(getBitmapFromUri(applicationContext, uri)!!)
        }
    }


    val startScanActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                var data = result.data?.getStringExtra(utils.QR_DATA)
                Log.d("DATAREVICE", data.toString())
                if (!data.isNullOrEmpty()) {
                    var reminderItem = stringToReminderDataObject(data)
                    autoFillData(reminderItem!!)
                }

            }
        }

    fun autoFillData(reminderItem: ReminderItem) {
        if (reminderItem != null) {
            binding.addReminderName.setText(reminderItem.name)
            binding.addReminderDob.setText(getDateToString(reminderItem.date, DATE_DD_MMM_YYY))
            binding.addReminderTime.setText(reminderItem.time)
            binding.addReminderContact.setText(reminderItem.mobileNumber)
            binding.addReminderWish.setText(reminderItem.wish)
            binding.addReminderTextmessage.isChecked = reminderItem.isTextMessage
            binding.addReminderWhatsappmessage.isChecked = reminderItem.isWhatsappMessage
            if (!reminderItem.imageWebUriPath.isNullOrEmpty()) {
                Glide.with(binding.imgProfile.context).asBitmap().load(reminderItem.imageWebUriPath)
                    .error(R.drawable.ic_profile_demo).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any?,
                            target: Target<Bitmap?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            _imgBitmap.postValue(resource!!)
                            return true
                        }
                    }).submit()


            }
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.menu_scan -> startScanActivity.launch(
                Intent(
                    applicationContext,
                    BarCodeScanerActivity::class.java
                )
            )

            else -> Log.d("MENU", "")
        }
        return true
    }


}