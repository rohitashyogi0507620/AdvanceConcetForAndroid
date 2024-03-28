package com.Yogify.birthdayreminder.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.Yogify.birthdayreminder.util.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ACTIVITYLIFECYCLE", "onViewCreated")
        getBundle()
        initViews()
        observers()
        onClickListener()
    }

    protected open fun getBundle() {}

    protected open fun initViews() {
    }

    protected open fun observers() {
    }

    protected open fun onClickListener() {
    }

}