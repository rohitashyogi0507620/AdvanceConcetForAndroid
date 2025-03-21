package com.Yogify.birthdayreminder.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.backup.DriveWorker
import com.Yogify.birthdayreminder.databinding.BottomsheetOptionmenuBinding
import com.Yogify.birthdayreminder.databinding.BottomsheetShowdetailsBinding
import com.Yogify.birthdayreminder.databinding.FragmentHomeBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.ui.activitys.AddReminderActivity
import com.Yogify.birthdayreminder.ui.activitys.BarCodeScanerActivity
import com.Yogify.birthdayreminder.ui.activitys.ProfileActivity
import com.Yogify.birthdayreminder.ui.adapters.ReminderAdpter
import com.Yogify.birthdayreminder.ui.adapters.ReminderSearchAdpter
import com.Yogify.birthdayreminder.ui.adapters.SearchAdapter
import com.Yogify.birthdayreminder.ui.notification.NotificationWorker
import com.Yogify.birthdayreminder.ui.viewmodels.MainViewModel
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.LAYOUT_GRID
import com.Yogify.birthdayreminder.util.utils.Companion.LAYOUT_LINEAR
import com.Yogify.birthdayreminder.util.utils.Companion.REMINDERITEM
import com.Yogify.birthdayreminder.util.utils.Companion.showAlertDialog
import com.Yogify.birthdayreminder.util.utils.Companion.showSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.search.SearchView.TransitionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Locale


@AndroidEntryPoint
class HomeFragment : BaseFragment(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {


    //  @set :Inject when we user private variable to inject
    //    @set:Inject
    //    @Nullable
    //    var googleAccount: GoogleSignInAccount? = null


    var searchlist = listOf<ReminderItem>()
    lateinit var binding: FragmentHomeBinding
    lateinit var reminderAdpter: ReminderAdpter
    var searchreminderAdpter: SearchAdapter?=null
    private val mainViewModel: MainViewModel by viewModels()
    var LAYOUT_TYPE = LAYOUT_GRID
    val SPEECH_REQUEST_CODE = 0
    var imageUri: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val uploadWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest)
        GoogleSignIn.getLastSignedInAccount(requireContext()).also { googleAccount ->
            if (googleAccount != null) {
                Glide.with(this).load(googleAccount?.photoUrl).centerCrop().sizeMultiplier(0.50f)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource.let {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    binding.searchBar.menu.findItem(R.id.menu_profile).icon =
                                        resource
                                }
                            }
                            return true
                        }


                    }).circleCrop().submit()
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.searchBar.menu.findItem(R.id.menu_profile)
                        .setIcon(R.drawable.ic_profile_demo)
                }
            }
        }

    }


    override fun getBundle() {
        super.getBundle()
    }

    override fun initViews() {
        super.initViews()

        binding.searchBar.setOnMenuItemClickListener(this)
        reminderAdpter = ReminderAdpter(LAYOUT_TYPE)
        binding.rvReminder.adapter = reminderAdpter


        binding.searchview.addTransitionListener { searchView, previousState, newState ->
            if (newState == TransitionState.SHOWING) {
                searchreminderAdpter = SearchAdapter(searchlist)
                binding.rvSearchReminder.adapter = searchreminderAdpter
                searchreminderAdpter?.setOnItemClickListener(object :
                    SearchAdapter.OnItemClickListner {
                    override fun onItemClick(type: Int, item: ReminderItem) {
                        binding.searchview.hide()
                        reminderAdpter.performItemClick(item)
                    }

                })
            }
        }
        binding.searchview.editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(theWatchedText: Editable) {}
        })

    }


    override fun observers() {
        super.observers()
        lifecycle.coroutineScope.launch {
            mainViewModel.getReminder().collect() {
                reminderAdpter.submitList(it)
                searchlist = it
            }
        }



        mainViewModel.insertReminder.observe(this, Observer {
            val inputData = Data.Builder().putString(utils.IMAGE_URL, imageUri)
                .putString(REMINDERITEM, it.toString()).build()
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
            override fun onItemClick(type: Int, item: ReminderItem) {
                if (type == 1) {
                    itemBottomSheeet(item)
                } else if (type == 2) {
                    optionMenuOnClick(item)
                }
            }

        })
        reminderAdpter.setOnItemLOngClickListener(object : ReminderAdpter.OnItemLongClickListner {
            override fun onItemLongClick(type: Int, item: ReminderItem) {
                optionMenuOnClick(item)
            }

        })

        binding.mainFbAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddReminderActivity::class.java))

        }
    }

    fun itemBottomSheeet(item: ReminderItem) {

        val bottomSheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogtheme)
        val bindingSheet = BottomsheetShowdetailsBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindingSheet.root)
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val window = bottomSheet.window
        window!!.findViewById<View>(com.google.android.material.R.id.container).fitsSystemWindows =
            false

        bindingSheet.txtName.setTextColor(Color.parseColor(item.colorDark))
        bindingSheet.txtName.text = item.name
        bindingSheet.imgProfile.strokeColor =
            ColorStateList.valueOf(Color.parseColor(item.colorLight))
        bindingSheet.viewDivider.setBackgroundColor(Color.parseColor(item.colorDark))
        bindingSheet.viewDividerBelow.setBackgroundColor(Color.parseColor(item.colorDark))
        bindingSheet.rlBackground.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(item.colorLight))
//        bindingSheet.imgDeleteOption.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item?.colorLight))
//        bindingSheet.imgEditOption.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item?.colorLight))
//        bindingSheet.imgQrcodeOption.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item?.colorLight))
//        bindingSheet.imgWhatsappOption.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item?.colorLight))
//        bindingSheet.imgShareOption.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item?.colorLight))

        bindingSheet.txtWish.setTextColor(Color.parseColor(item.colorDark))
        bindingSheet.txtWish.text = item.wish
        bindingSheet.txtDateTime.setTextColor(Color.parseColor(item.colorDark))
        bindingSheet.txtDateTime.text = utils.longToDate(item.date, utils.DATE_DD_MMM_YYY_HH_MM)
        bindingSheet.txtDays.setTextColor(Color.parseColor(item.colorDark))
        bindingSheet.txtDays.text =
            "${getString(R.string.turing)} ${(utils.calculateAge(item.date) + 1).toString()} on ${
                utils.longToDate(
                    item.date, utils.DATE_MMMM_DD
                )
            }"

        Glide.with(requireContext()).load(item.imageUri).centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.ic_profile_demo)
            .into(bindingSheet.imgProfile)

        bindingSheet.imgProfile.setOnClickListener {
            if (!item.imageUri.equals("null")) utils.showFullSizeImageDialog(
                requireContext(), item.imageUri
            )
        }

        bindingSheet.menuOptionDelete.setOnClickListener {
            bottomSheet.dismiss()
            var dialog = showAlertDialog(
                requireContext(),
                R.raw.delete_animation,
                getString(R.string.deletetitle),
                getString(R.string.deletesubtitle),
                getString(R.string.delete)
            )
            var postiveBtn = dialog.findViewById<Button>(R.id.alertBtnPositive)
            postiveBtn.setOnClickListener {
                dialog.dismiss()
                mainViewModel.deleteReminder(item)
            }

        }
        bindingSheet.menuOptionEdit.setOnClickListener {
            bottomSheet.dismiss()
            startActivity(
                Intent(requireActivity(), AddReminderActivity::class.java).putExtra(
                    utils.EDIT_REMINDER, utils.reminderDataObjectToString(item)
                ).putExtra(utils.IS_EDIT_REMINDER, true)
            )
        }
        bindingSheet.menuOptionQr.setOnClickListener {
            createBarcode(bindingSheet, item)
        }
        bindingSheet.menuOptionShareWhatsapp.setOnClickListener {
            createBarcode(bindingSheet, item, true, true)
        }
        bindingSheet.menuOptionShare.setOnClickListener {
            createBarcode(bindingSheet, item, true)
        }


        if (!bottomSheet.isShowing) bottomSheet.show()


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
        bindingSheet.qrCancle.imageTintList =
            ColorStateList.valueOf(Color.parseColor(item?.colorDark))

        Glide.with(requireContext()).load(item.imageUri).centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.ic_profile_demo)
            .into(bindingSheet.imgProfile)

//        bindingSheet.menuOptionDelete.setOnClickListener {
//            bottomSheet.dismiss()
//            var dialog = showAlertDialog(
//                requireContext(),
//                R.raw.delete_animation,
//                getString(R.string.deletetitle),
//                getString(R.string.deletesubtitle),
//                getString(R.string.delete)
//            )
//            var postiveBtn = dialog.findViewById<Button>(R.id.alertBtnPositive)
//            postiveBtn.setOnClickListener {
//                dialog.dismiss()
//                mainViewModel.deleteReminder(item)
//            }
//
//        }
//        bindingSheet.menuOptionEdit.setOnClickListener {
//            bottomSheet.dismiss()
//            startActivity(
//                Intent(requireActivity(), AddReminderActivity::class.java).putExtra(
//                    utils.EDIT_REMINDER, utils.reminderDataObjectToString(item)
//                ).putExtra(utils.IS_EDIT_REMINDER, true)
//            )
//        }
//        bindingSheet.menuOptionQr.setOnClickListener {
//            createBarcode(bindingSheet, item)
//        }
//        bindingSheet.menuOptionShareWhatsapp.setOnClickListener {
//            createBarcode(bindingSheet, item, true, true)
//        }
//        bindingSheet.menuOptionShare.setOnClickListener {
//            createBarcode(bindingSheet, item, true)
//        }

        if (!bottomSheet.isShowing) bottomSheet.show()
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
            R.id.menu_scan -> startScanActivity.launch(
                Intent(
                    activity, BarCodeScanerActivity::class.java
                )
            )

            R.id.menu_profile -> startActivity(
                Intent(
                    requireContext(), ProfileActivity::class.java
                )
            )

            else -> Log.d("MENU", "")
        }
        return true
    }


    val startScanActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                var data = result.data?.getStringExtra(utils.QR_DATA)
                Log.d("DATAREVICE", data.toString())
                if (!data.isNullOrEmpty()) {

                    var reminderItem = utils.stringToReminderDataObject(data)
                    if (!reminderItem?.imageWebUriPath.isNullOrEmpty()) {
                        Glide.with(requireContext()).asBitmap().load(reminderItem?.imageWebUriPath)
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
                                    imageUri = utils.saveImageintoStorage(
                                        resource,
                                        reminderItem?.name + reminderItem?.date?.toInt().toString(),
                                        utils.imageFolderPath(requireContext())
                                    )
                                    reminderItem?.imageUriPath = imageUri!!
                                    if (reminderItem != null) mainViewModel.insertReminder(
                                        reminderItem
                                    )
                                    return true
                                }

                            }).submit()

                    } else {
                        if (reminderItem != null) mainViewModel.insertReminder(reminderItem)
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


    fun createBarcode(
        bindingSheet: BottomsheetShowdetailsBinding,
        item: ReminderItem,
        isShare: Boolean = false,
        iswhatsapp: Boolean = false
    ) {
        if (item != null) {
            var qrbitmap = utils.showQrcode(utils.reminderDataObjectToString(item))
            Glide.with(this).load(qrbitmap).addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {

                    resource.let {
                        lifecycleScope.launch(Dispatchers.Main) {
                            bindingSheet.qrImage.setImageDrawable(resource)
                            bindingSheet.qrCancle.visibility = View.VISIBLE
                            bindingSheet.llOption.visibility = View.GONE
                            bindingSheet.qrCancle.setOnClickListener {
                                bindingSheet.qrCancle.visibility = View.GONE
                                bindingSheet.qrImage.setImageDrawable(null)
                                bindingSheet.llOption.visibility = View.VISIBLE

                            }
                            if (isShare) {
                                shareReminder(iswhatsapp, bindingSheet.moreBottomsheetMore, item)
                            }
                        }
                    }

                    return true
                }


            }).submit()
        }
    }

    fun shareReminder(iswhatsapp: Boolean = false, view: View, item: ReminderItem) {
        try {
            val imageUri = convertToImage(view)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, item.wish)
            if (iswhatsapp) {
                try {
                    shareIntent.setPackage("com.whatsapp")
                    startActivity(shareIntent)
                } catch (e: Exception) {
                    showSnackbar(binding.root, getString(R.string.whatsappNotFound))
                }
            } else {
                startActivity(Intent.createChooser(shareIntent, getString(R.string.shareReminder)))
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun convertToImage(view: View): Uri {
        val imgbitmap = createBitmapFromView(view)
        return getImageUri(requireContext(), imgbitmap)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            getString(R.string.app_name) + System.currentTimeMillis().toString(),
            null
        )
        return Uri.parse(path)
    }

    private fun createBitmapFromView(view: View): Bitmap {
        val totalHeight = view.height
        val totalWidth = view.width
        val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    fun filter(text: String) {
        val filteredlist = mutableListOf<ReminderItem>()

        for (item in searchlist) {
            if (item.name.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                filteredlist.add(item)
            }
        }
        searchreminderAdpter?.filterList(filteredlist)

    }

    fun changeLayout(menuItem: MenuItem) {
        if (LAYOUT_TYPE == LAYOUT_LINEAR) {
            LAYOUT_TYPE = LAYOUT_GRID
            binding.rvReminder.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            menuItem.setIcon(resources.getDrawable(R.drawable.view_linear))
            reminderAdpter.changeLayout(LAYOUT_TYPE)
            binding.rvReminder.adapter = reminderAdpter

        } else if (LAYOUT_TYPE == LAYOUT_GRID) {
            LAYOUT_TYPE = LAYOUT_LINEAR
            binding.rvReminder.layoutManager = LinearLayoutManager(requireContext())
            menuItem.setIcon(resources.getDrawable(R.drawable.view_grid))
            reminderAdpter.changeLayout(LAYOUT_TYPE)
            binding.rvReminder.adapter = reminderAdpter
        }

    }

}