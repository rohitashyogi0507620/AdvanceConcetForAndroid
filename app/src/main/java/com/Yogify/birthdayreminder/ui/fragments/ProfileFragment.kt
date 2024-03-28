package com.Yogify.birthdayreminder.ui.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.backup.DriveHelper
import com.Yogify.birthdayreminder.databinding.FragmentProfileBinding
import com.Yogify.birthdayreminder.ui.viewmodels.MainViewModel
import com.Yogify.birthdayreminder.util.Constants.ABOUT_DEVELOPER
import com.Yogify.birthdayreminder.util.Constants.PRIVECY_POLICY
import com.Yogify.birthdayreminder.util.utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.annotation.Nullable
import javax.inject.Inject

class ProfileFragment : BaseFragment() {

    lateinit var binding: FragmentProfileBinding
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @set:Inject
    @Nullable
    var googleAccount: GoogleSignInAccount? = null

    @set:Inject
    @Nullable
    var driveHelper: DriveHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun getBundle() {
        super.getBundle()

    }

    override fun initViews() {
        super.initViews()
        if (googleAccount != null) processAccountInformation(googleAccount!!)
        setVersionCode()
    }

    override fun observers() {
        super.observers()

    }

    override fun onClickListener() {
        super.onClickListener()
        binding.btnlogin.setOnClickListener {
            requestSignIn()
        }
        binding.btnlogout.setOnClickListener {
            googleSignInClient.signOut().addOnSuccessListener {
                utils.showSnackbar(binding.root, getString(R.string.logoutsuccessfully))
            }
        }
        binding.btnRevokeAccess.setOnClickListener {
            googleSignInClient.revokeAccess().addOnSuccessListener {
                utils.showSnackbar(binding.root, getString(R.string.accountDeleted))
            }
        }
        binding.btnAccessDrive.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                driveHelper?.createFolder(getString(R.string.app_name))
            }
        }
        binding.btnuploadFile.setOnClickListener {}

        binding.stLlRating.setOnClickListener {
            rating()
        }
        binding.stLlShareapp.setOnClickListener {
            shareApp()
        }
        binding.stLlMoreapps.setOnClickListener {
            moreApps()
        }
        binding.stLlDeveloperContact.setOnClickListener {
            aboutDeveloper()
        }
        binding.stLlPrivecyPolicy.setOnClickListener {
            privecyPolicy()
        }

    }

    private fun moreApps() {
        try {
            val appStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + requireContext().getPackageName())
            )
            appStoreIntent.setPackage("com.android.vending")
            startActivity(appStoreIntent)
        } catch (exception: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + requireContext().getPackageName())
                )
            )
        }
    }

    private fun privecyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVECY_POLICY))
        startActivity(intent)
    }

    private fun aboutDeveloper() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ABOUT_DEVELOPER))
        startActivity(intent)
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
            var shareMessage = resources.getString(R.string.shareapp_message)
            shareMessage = "${shareMessage} https://play.google.com/store/apps/details?id=${requireContext().packageName}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share with"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun rating() {
        try {
            val rateIntent: Intent = rateIntentForUrl("market://details")
            startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val rateIntent: Intent = rateIntentForUrl("https://play.google.com/store/apps/details")
            startActivity(rateIntent)
        }
    }

    fun rateIntentForUrl(url: String): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(String.format("%s?id=%s", url, requireContext().packageName))
        )
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = if (Build.VERSION.SDK_INT >= 21) {
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        }
        intent.addFlags(flags)
        return intent
    }


    fun setVersionCode() {
        try {
            val pInfo: PackageInfo =
                requireContext().getPackageManager().getPackageInfo(requireContext().packageName, 0)
            val version = pInfo.versionName
            binding.stTxtVersionCode.setText(version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }


    fun requestSignIn() {
        startGoogleSignInActivity.launch(googleSignInClient.getSignInIntent())
    }

    val startGoogleSignInActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        .addOnSuccessListener { googleAccount ->
                            processAccountInformation(googleAccount)
                        }.addOnFailureListener { exception ->
                            exception.printStackTrace()
                        }
                }
            }
        }

    fun processAccountInformation(googleAccount: GoogleSignInAccount) {
        Log.d("GoogleAccount", googleAccount.toString())
        binding.stTxtUsername.text = googleAccount.displayName.toString()
        Glide.with(requireContext()).load(googleAccount.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).error(R.drawable.ic_profile_demo)
            .into(binding.stImgProfile)
    }


}