package com.Yogify.birthdayreminder.ui.activitys

import android.os.Bundle
import androidx.activity.viewModels
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.databinding.ActivityProfileBinding
import com.Yogify.birthdayreminder.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    lateinit var binding: ActivityProfileBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }



}