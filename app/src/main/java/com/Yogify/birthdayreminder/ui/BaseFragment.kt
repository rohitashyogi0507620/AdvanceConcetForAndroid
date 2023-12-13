package com.Yogify.birthdayreminder.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

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