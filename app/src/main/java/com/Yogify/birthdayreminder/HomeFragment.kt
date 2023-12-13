package com.Yogify.birthdayreminder

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.Yogify.birthdayreminder.backup.DriveWorker
import com.Yogify.birthdayreminder.databinding.BottomsheetOptionmenuBinding
import com.Yogify.birthdayreminder.databinding.FragmentHomeBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.ui.AddReminderActivity
import com.Yogify.birthdayreminder.ui.BarCodeScanerActivity
import com.Yogify.birthdayreminder.ui.BaseFragment
import com.Yogify.birthdayreminder.ui.MainViewModel
import com.Yogify.birthdayreminder.ui.ReminderAdpter
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_DD_MMM_YYY
import com.Yogify.birthdayreminder.util.utils.Companion.QR_DATA
import com.Yogify.birthdayreminder.util.utils.Companion.getDateToLong
import com.Yogify.birthdayreminder.util.utils.Companion.getDateToString
import com.Yogify.birthdayreminder.util.utils.Companion.imageFolderPath
import com.Yogify.birthdayreminder.util.utils.Companion.reminderDataObjectToString
import com.Yogify.birthdayreminder.util.utils.Companion.saveImageintoStorage
import com.Yogify.birthdayreminder.util.utils.Companion.showQrcode
import com.Yogify.birthdayreminder.util.utils.Companion.stringToReminderDataObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.annotation.Nullable
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    lateinit var binding: FragmentHomeBinding
    lateinit var reminderAdpter: ReminderAdpter
    private val mainViewModel: MainViewModel by viewModels()
    var LAYOUT_TYPE = 1
    val SPEECH_REQUEST_CODE = 0
    var imageUri: String? = null

    // @set :Inject when we user private variable to inject
    @set:Inject
    @Nullable
    var googleAccount: GoogleSignInAccount? = null

    fun changeLayout(menuItem: MenuItem) {
        if (LAYOUT_TYPE == 0) {
            LAYOUT_TYPE = 1
            binding.rvReminder.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            menuItem.setIcon(resources.getDrawable(R.drawable.view_linear))

        } else if (LAYOUT_TYPE == 1) {
            LAYOUT_TYPE = 0
            binding.rvReminder.layoutManager = LinearLayoutManager(requireContext())
            menuItem.setIcon(resources.getDrawable(R.drawable.view_grid))

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getBundle() {
        super.getBundle()

    }

    override fun initViews() {
        super.initViews()
        binding.searchBar.setOnMenuItemClickListener(this)
        reminderAdpter = ReminderAdpter()
        binding.rvReminder.adapter = reminderAdpter
        if (googleAccount != null) {
            Glide.with(this).load(googleAccount?.photoUrl).centerCrop().sizeMultiplier(0.50f)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.let {
                            lifecycleScope.launch(Dispatchers.Main) {
                                binding.searchBar.menu.findItem(R.id.menu_profile).icon = resource
                            }
                        }
                        return true
                    }

                }).circleCrop().submit()
            binding.searchBar.setHint(googleAccount?.displayName)
        } else {
            binding.searchBar.setHint(getString(R.string.search_hint))
        }

    }

    override fun observers() {
        super.observers()
        lifecycle.coroutineScope.launch {
            mainViewModel.getReminder().collect() {
                reminderAdpter.submitList(it)
            }
        }

        mainViewModel.insertReminder.observe(this, Observer {
            val inputData = Data.Builder().putString(utils.IMAGE_URL, imageUri)
                .putString(utils.REMINDER_ID, it.toString()).build()
            val uploadDataConstraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val uploadWorkRequest = OneTimeWorkRequestBuilder<DriveWorker>().setInputData(inputData)
                .setConstraints(uploadDataConstraints).build()
            WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest)
            utils.showSnackbar(binding.root, getString(R.string.reminderaddedsuccessfully))

        })
    }

    override fun onClickListener() {
        super.onClickListener()
        reminderAdpter.setOnItemClickListener(object : ReminderAdpter.OnItemClickListner {
            override fun onItemClick(view: View, type: Int, reminderItem: ReminderItem) {

                optionMenuOnClick(reminderItem)
//                PopupMenu(requireContext(), view).apply {
//                    setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
//                        override fun onMenuItemClick(item: MenuItem?): Boolean {
//                            return when (item?.itemId) {
//
//                                R.id.menu_option_delete -> {
//                                    mainViewModel.deleteReminder(reminderItem)
//                                    true
//                                }
//
//                                R.id.menu_option_edit -> {
//                                    //editReminder()
//                                    true
//                                }
//
//                                else -> false
//                            }
//                        }
//
//                    })
//
//                    inflate(R.menu.menu_option_reminder)
//                    setForceShowIcon(true)
//                    show()
//                }
            }

        })
        binding.mainFbAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddReminderActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        var menuItem = menu.findItem(R.id.menu_view)
        changeLayout(menuItem)

    }


    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.menu_notification -> Log.d("MENU", getString(R.string.profile))
            R.id.menu_voice_search -> displaySpeechRecognizer()
            R.id.menu_view -> changeLayout(menuItem)
            R.id.menu_scan -> startScanActivity.launch(Intent(activity, BarCodeScanerActivity::class.java))
            else -> Log.d("MENU", "")
        }
        return true
    }

    val startScanActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                var data = result.data?.getStringExtra(QR_DATA)
                Log.d("DATAREVICE", data.toString())
                if (!data.isNullOrEmpty()) {

                    var reminderItem = stringToReminderDataObject(data)
                    if (!reminderItem?.imageWebUriPath.isNullOrEmpty()) {
                        Glide.with(requireContext()).asBitmap()
                            .load(reminderItem?.imageWebUriPath)
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
                                    imageUri = saveImageintoStorage(
                                        resource!!,
                                        reminderItem?.name + getDateToLong(
                                            getDateToString(
                                                reminderItem?.date!!,
                                                DATE_DD_MMM_YYY
                                            ), DATE_DD_MMM_YYY
                                        ).time.toString(),
                                        imageFolderPath(requireContext())
                                    )
                                    reminderItem?.imageUriPath = imageUri!!
                                    if (reminderItem != null) mainViewModel.insertReminder(
                                        reminderItem!!
                                    )
                                    return true
                                }
                            }).submit()


                    } else {
                        if (reminderItem != null) mainViewModel.insertReminder(reminderItem!!)
                    }

                }

            }
        }


    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.let { results -> results[0] }
            binding.searchBar.setText(spokenText)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun optionMenuOnClick(item: ReminderItem) {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogtheme)
        val bindingSheet = BottomsheetOptionmenuBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindingSheet.root)
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val window = bottomSheet.window
        window!!.findViewById<View>(com.google.android.material.R.id.container).fitsSystemWindows =
            false
        bindingSheet.txtName.setTextColor(Color.parseColor(item.colorDark))
        bindingSheet.txtName.setText(item.name)
        bindingSheet.imgProfile.strokeColor =
            ColorStateList.valueOf(Color.parseColor(item?.colorDark))
        bindingSheet.viewDivider.setBackgroundColor(Color.parseColor(item.colorDark))
        bindingSheet.rlBackground.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(item?.colorLight))
        bindingSheet.llQr.qrCancle.imageTintList =
            ColorStateList.valueOf(Color.parseColor(item?.colorDark))


        var bitmap: Bitmap? = null
        Glide.with(bindingSheet.imgProfile.context).asBitmap().load(item.imageUri)
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
                    bitmap = resource
                    bindingSheet.imgProfile.setImageBitmap(bitmap)
                    return true
                }
            }).submit()

        bindingSheet.menuOptionDelete.setOnClickListener {
            mainViewModel.deleteReminder(item)
            bottomSheet.dismiss()
        }
        bindingSheet.menuOptionQr.setOnClickListener {
            if (bitmap != null && item != null) {
                var qrbitmap = showQrcode(reminderDataObjectToString(item))
                Glide.with(bindingSheet.llQr.qrImage.context).load(qrbitmap)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(bindingSheet.llQr.qrImage)
                bindingSheet.llQr.root.visibility = VISIBLE
                bindingSheet.llOption.visibility = GONE
                bindingSheet.llQr.qrCancle.setOnClickListener {
                    bindingSheet.llQr.root.visibility = GONE
                    bindingSheet.llOption.visibility = VISIBLE

                }
            }
        }

        bottomSheet.show()
    }


}