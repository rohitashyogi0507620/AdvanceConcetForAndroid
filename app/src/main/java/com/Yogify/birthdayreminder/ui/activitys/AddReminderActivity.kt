package com.Yogify.birthdayreminder.ui.activitys

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
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
import com.Yogify.birthdayreminder.ui.adapters.ColorAdpter
import com.Yogify.birthdayreminder.ui.viewmodels.MainViewModel
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_DD_MMM_YYY
import com.Yogify.birthdayreminder.util.utils.Companion.EDIT_REMINDER
import com.Yogify.birthdayreminder.util.utils.Companion.GENDER_FEMALE
import com.Yogify.birthdayreminder.util.utils.Companion.GENDER_MALE
import com.Yogify.birthdayreminder.util.utils.Companion.NOTIFICATION_TYPE_ALL
import com.Yogify.birthdayreminder.util.utils.Companion.NOTIFICATION_TYPE_ONE_MONTH
import com.Yogify.birthdayreminder.util.utils.Companion.NOTIFICATION_TYPE_SAMEDAY
import com.Yogify.birthdayreminder.util.utils.Companion.NOTIFICATION_TYPE_SEVEN_DAY
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDER_TYPE_ANNIVERSARY
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDER_TYPE_BIRTHDAY
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDER_TYPE_OTHER
import com.Yogify.birthdayreminder.util.utils.Companion.TIME_HH
import com.Yogify.birthdayreminder.util.utils.Companion.TIME_HH_MM
import com.Yogify.birthdayreminder.util.utils.Companion.TIME_MM
import com.Yogify.birthdayreminder.util.utils.Companion.cameraStoragePermission
import com.Yogify.birthdayreminder.util.utils.Companion.checkNotificationPermission
import com.Yogify.birthdayreminder.util.utils.Companion.colorTheme
import com.Yogify.birthdayreminder.util.utils.Companion.getBitmapFromUri
import com.Yogify.birthdayreminder.util.utils.Companion.getLongtoFormate
import com.Yogify.birthdayreminder.util.utils.Companion.imageFolderPath
import com.Yogify.birthdayreminder.util.utils.Companion.longToDate
import com.Yogify.birthdayreminder.util.utils.Companion.saveImageintoStorage
import com.Yogify.birthdayreminder.util.utils.Companion.sendTextSMS
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
import java.util.Date
import java.util.Locale
import javax.annotation.Nullable
import javax.inject.Inject


@AndroidEntryPoint
class AddReminderActivity : BaseActivity(),
    androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    lateinit var binding: ActivityAddReminderBinding
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var timePicker: MaterialTimePicker
    lateinit var datePicker: MaterialDatePicker<Long>

    var imageUri: String? = null
    var reminderType: Int = 0
    var notificationType: Int = 0
    var gender: Int = 1
    val colorAdpter = ColorAdpter()
    var themeColor: ThemeColor? = null
    lateinit var reminderItem: ReminderItem
    var isEditReminder: Boolean = false

    var calendar = Calendar.getInstance(Locale.US)
    var minute = 0
    var hour = 0
    var datelong: Long = 0


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

    }

    private fun observer() {
        mainViewModel.insertReminder.observe(this, Observer {

            val inputData = Data.Builder().putString(utils.IMAGE_URL, imageUri)
                .putString(utils.REMINDERITEM, it.toString()).build()
            val uploadDataConstraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val uploadWorkRequest = OneTimeWorkRequestBuilder<DriveWorker>().setInputData(inputData)
                .setConstraints(uploadDataConstraints).build()
            WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)
            showSnackbar(
                binding.root,
                if (isEditReminder) getString(R.string.reminderupdatedsuccessfully) else getString(R.string.reminderaddedsuccessfully)
            )
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
            Glide.with(applicationContext).load(it).diskCacheStrategy(DiskCacheStrategy.NONE)
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

        setSupportActionBar(binding.toolbarBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (!checkNotificationPermission(applicationContext)) requestNotificationPermission.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
        binding.addReminderColorList.adapter = colorAdpter
        colorAdpter.submitList(colorTheme(applicationContext))


        binding.toolbarBar.setOnMenuItemClickListener(this)

        if (intent != null && intent.hasExtra(EDIT_REMINDER)) {
            reminderItem = stringToReminderDataObject(intent.getStringExtra(EDIT_REMINDER)!!)!!
            isEditReminder = intent.getBooleanExtra(utils.IS_EDIT_REMINDER, false)
            if (isEditReminder) autoFillData()
        }

        timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText(getString(R.string.select_time))
            .setHour(calendar.get(Calendar.HOUR_OF_DAY)).setMinute(calendar.get(Calendar.MINUTE))
            .build()
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(calendar.timeInMillis)
            .build()


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

            calendar.timeInMillis = datelong
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.HOUR, hour)

            if (imgBitmap.value != null) {
                imageUri = saveImageintoStorage(
                    imgBitmap.value!!,
                    binding.addReminderName.text.toString().trim() + calendar.timeInMillis,
                    imageFolderPath(applicationContext)
                )
            }

            reminderItem = ReminderItem(
                if (isEditReminder) reminderItem.id else 0,
                imageUri.toString(),
                if (isEditReminder) reminderItem.imageWebUriPath else "",
                binding.addReminderName.text.toString().trim(),
                gender,
                calendar.timeInMillis,
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
            if (isEditReminder) mainViewModel.updateReminder(reminderItem)
            else mainViewModel.insertReminder(reminderItem)

        }

        binding.addReminderWhatsappmessage.setOnCheckedChangeListener { compoundButton, ischecked ->
            // sendTextWhatsapp(applicationContext,   binding.addReminderContact.text.toString().trim(), binding.addReminderWish.text.toString().trim())
        }
        binding.addReminderTextmessage.setOnCheckedChangeListener { compoundButton, ischecked ->
            // singlePermission.launch(Manifest.permission.SEND_SMS)
        }



        binding.addReminderTime.setOnClickListener {
            timePicker.show(supportFragmentManager, "TIME_PICKER")
        }

        timePicker.addOnPositiveButtonClickListener {
            hour = timePicker.hour
            minute = timePicker.minute
            binding.addReminderTime.setText("$hour:$minute")
        }

        binding.addReminderDob.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            datelong = it
            binding.addReminderDob.setText(getLongtoFormate(it, DATE_DD_MMM_YYY))
        }

        binding.addReminderContact.setOnClickListener {
            contactNumberPicker.launch()
            getPerson.launch(1)
//            val i = Intent(Intent.ACTION_PICK)
//            i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
//            startActivityForResult(i, 100)
        }


    }

    val singlePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                sendTextSMS(
                    binding.addReminderContact.text.toString().trim(),
                    binding.addReminderWish.text.toString().trim()
                )
            }
        }

    val getPerson = registerForActivityResult(PickContact()) {
        it?.also { contactUri ->
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
            )

            contentResolver?.query(contactUri, projection, null, null, null)?.apply {
                moveToFirst()
                Log.d("DATA", getString(0) + "//" + getString(1) + "//" + getString(2))
                close()
            }
        }
    }

    class PickContact : ActivityResultContract<Int, Uri>() {
        override fun createIntent(context: Context, input: Int): Intent {
            return Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).also {
                it.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return if (resultCode == RESULT_OK) intent?.data!! else Uri.parse("")
        }


    }

    val contactNumberPicker =
        registerForActivityResult(ActivityResultContracts.PickContact()) { it ->
            Log.d("DATA", it.toString())
            try {

                val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val cursor = contentResolver.query(
                    it!!, projection, null, null, null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val numberIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val phonenumber = cursor.getString(numberIndex)
                    binding.addReminderContact.setText(phonenumber)
                }

                cursor!!.close()

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
                    reminderItem = stringToReminderDataObject(data)!!
                    autoFillData()
                }

            }
        }

    fun autoFillData() {
        if (reminderItem != null) {

            calendar.timeInMillis = reminderItem.date
            datelong = reminderItem.date
            hour = longToDate(reminderItem.date, TIME_HH).toInt()
            minute = longToDate(reminderItem.date, TIME_MM).toInt()

            binding.addReminderName.setText(reminderItem.name)
            binding.addReminderDob.setText(longToDate(reminderItem.date, DATE_DD_MMM_YYY))
            binding.addReminderTime.setText("$hour:$minute")
            binding.addReminderContact.setText(reminderItem.mobileNumber)
            binding.addReminderWish.setText(reminderItem.wish)
            binding.addReminderTextmessage.isChecked = reminderItem.isTextMessage
            binding.addReminderWhatsappmessage.isChecked = reminderItem.isWhatsappMessage
            if (!reminderItem.imageUri.isNullOrEmpty()) {
                Glide.with(binding.imgProfile.context).asBitmap().load(reminderItem.imageUri)
                    .error(R.drawable.ic_profile_demo).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            model: Any,
                            target: Target<Bitmap?>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            _imgBitmap.postValue(resource!!)
                            return true
                        }

                    }).submit()


            }
            colorAdpter.autoSetColor(reminderItem.colorLight, reminderItem.colorDark)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_reminder, menu)
        return true
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.menu_scan -> startScanActivity.launch(
                Intent(
                    applicationContext, BarCodeScanerActivity::class.java
                )
            )

            else -> Log.d("MENU", "")
        }
        return true
    }


}