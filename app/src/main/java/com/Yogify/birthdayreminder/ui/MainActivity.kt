package com.Yogify.birthdayreminder.ui

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.databinding.ActivityMainBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.getDateToLong
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseActivity(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var adapter: Adpter
    private lateinit var reminderAdpter: ReminderAdpter
    val SPEECH_REQUEST_CODE = 0
    var LAYOUT_TYPE = 0
    var TAG = this.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.searchBar.setOnMenuItemClickListener(this)

//         binding.searchView.inflateMenu(R.menu.menu_searchview)
//        binding.searchView.setOnMenuItemClickListener(this)
//        binding.searchView.getEditText().setOnEditorActionListener { v, actionId, event ->
//            binding.searchBar.setText(binding.searchView.getText())
//            binding.searchView.hide()
//            false
//        }
        loadImage()

//        binding.searchView.addTransitionListener { searchView, previousState, newState ->
//            if (newState === SearchView.TransitionState.SHOWING) {
//
//            } else if (newState === SearchView.TransitionState.HIDING) {
//
//            }
//        }


        //adapter = Adpter()
        //  binding.layoutSearch.rvSearch.layoutManager = LinearLayoutManager(this)
        // binding.layoutSearch.rvSearch.adapter = adapter

        reminderAdpter = ReminderAdpter()
        binding.rvReminder.adapter = reminderAdpter
        reminderAdpter.setOnItemClickListener(object : ReminderAdpter.OnItemClickListner {
            override fun onItemClick(item: ReminderItem) {
                mainViewModel.deleteReminder(item)
            }

        })
        binding.mainFbAdd.setOnClickListener {
            startActivity(Intent(this, AddReminderActivity::class.java))
        }

        mainViewModel.insertReminder.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        lifecycle.coroutineScope.launch {
            mainViewModel.getReminder().collect() {
                Log.d("DATA", it.toString())
                reminderAdpter.submitList(it)
            }
        }


    }


    private fun loadImage() {
        try {
            val profileUrl: String =
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm_FERoNLrDVgAEMAZx6Q1VpwZYZdN6K3Grw&usqp=CAU"

            Glide.with(this).load(profileUrl).centerCrop().circleCrop()
                .sizeMultiplier(0.50f) //optional
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "loadImage: ${e?.message}")
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.let {
                            renderProfileImage(it)
                        }
                        return true
                    }

                }).submit()

        } catch (e: Exception) {
            Log.e(TAG, "loadImage: ${e.message}")
        }
    }

    private fun renderProfileImage(resource: Drawable) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.searchBar.menu.findItem(R.id.menu_profile).icon = resource
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

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.menu_notification -> Log.d("MENU", getString(R.string.profile))
            R.id.menu_voice_search -> displaySpeechRecognizer()
            R.id.menu_view -> changeLayoutManager()
            else -> Log.d("MENU", "")
        }
        return true
    }

    private fun changeLayoutManager() {
        if (LAYOUT_TYPE == 0) {
            LAYOUT_TYPE = 1
            binding.rvReminder.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else if (LAYOUT_TYPE == 1) {
            LAYOUT_TYPE = 0
            binding.rvReminder.layoutManager = LinearLayoutManager(this)

        }
    }

}