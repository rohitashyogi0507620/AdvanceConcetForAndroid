package com.Yogify.birthdayreminder.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.databinding.ActivityMainBinding
import com.Yogify.birthdayreminder.db.DataBaseConstant.DATABASE_NAME_WITH_FORMAT
import com.Yogify.birthdayreminder.model.ReminderItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    var TAG = this.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHostFragment.navController)


//         binding.searchView.inflateMenu(R.menu.menu_searchview)
//        binding.searchView.setOnMenuItemClickListener(this)
//        binding.searchView.getEditText().setOnEditorActionListener { v, actionId, event ->
//            binding.searchBar.setText(binding.searchView.getText())
//            binding.searchView.hide()
//            false
//        }

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


        mainViewModel.dataRestore.observe(this, Observer {
            Toast.makeText(this, "Restore Done", Toast.LENGTH_SHORT).show()
        })

        binding.backup.setOnClickListener {
            mainViewModel.backupDatabase(applicationContext)
            //  exportDatabaseFile(applicationContext)
        }
        binding.restore.setOnClickListener {
            mainViewModel.restoreDatabase(applicationContext)
            // importDatabaseFile(applicationContext)
        }


    }


}