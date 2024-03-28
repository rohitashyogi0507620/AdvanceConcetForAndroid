package com.Yogify.birthdayreminder.ui.activitys

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.databinding.ActivityMainBinding
import com.Yogify.birthdayreminder.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


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