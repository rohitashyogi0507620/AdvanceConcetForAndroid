package com.Yogify.birthdayreminder

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.Yogify.birthdayreminder.backup.DriveHelper
import com.Yogify.birthdayreminder.backup.DriveWorker
import com.Yogify.birthdayreminder.databinding.FragmentProfileBinding
import com.Yogify.birthdayreminder.ui.BaseFragment
import com.Yogify.birthdayreminder.ui.MainViewModel
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.showSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.annotation.Nullable
import javax.inject.Inject

@AndroidEntryPoint
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
        if (googleAccount != null) {
            binding.accountData.setText(googleAccount?.displayName.toString())
            Glide.with(requireContext()).load(googleAccount?.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).error(R.drawable.ic_profile_demo)
                .into(binding.imgProfile)
        }
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
                showSnackbar(binding.root, getString(R.string.logoutsuccessfully))
            }
        }
        binding.btnRevokeAccess.setOnClickListener {
            googleSignInClient.revokeAccess().addOnSuccessListener {
                showSnackbar(binding.root, getString(R.string.accountDeleted))
            }
        }
        binding.btnAccessDrive.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                driveHelper?.createFolder(getString(R.string.app_name))
            }
        }
        binding.btnuploadFile.setOnClickListener {
            val inputData = Data.Builder().putString(utils.IMAGE_URL, "/storage/emulated/0/Download/Birthday Reminder/Pictures/toluyogu1702458973573").putString(utils.REMINDER_ID, "6").build()
            val uploadDataConstraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val uploadWorkRequest = OneTimeWorkRequestBuilder<DriveWorker>().setInputData(inputData).setConstraints(uploadDataConstraints).build()
            WorkManager.getInstance(requireContext()).enqueue(uploadWorkRequest)
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
        binding.accountData.setText(googleAccount?.displayName.toString())
        Glide.with(requireContext()).load(googleAccount.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgProfile)
    }


}