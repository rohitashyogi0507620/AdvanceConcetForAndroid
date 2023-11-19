package com.yogify.advanceconcetforandroid.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yogify.advanceconcetforandroid.databinding.ActivityMainBinding
import com.yogify.advanceconcetforandroid.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var adapter: BusAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = BusAdapter()
        binding.rvBusStand.layoutManager = LinearLayoutManager(this)
        binding.rvBusStand.adapter = adapter

        mainViewModel.res.observe(this, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvBusStand.visibility = View.VISIBLE
                    it.data.let {res->
                        if (res?.status == "SUCCESS"){
                            res.data?.let { it1 -> adapter.submitList(it1) }
                        }else{
                            Snackbar.make(binding.root, "Status = false",Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvBusStand.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvBusStand.visibility = View.VISIBLE
                    Snackbar.make(binding.root, "Something went wrong",Snackbar.LENGTH_SHORT).show()
                }
            }
        })

    }


}