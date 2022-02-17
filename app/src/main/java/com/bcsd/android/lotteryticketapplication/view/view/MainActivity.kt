package com.bcsd.android.lotteryticketapplication.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.ActivityMainBinding
import com.bcsd.android.lotteryticketapplication.view.model.LotteryNumber
import com.bcsd.android.lotteryticketapplication.view.service.MainService
import com.bcsd.android.lotteryticketapplication.view.view.homeFragment.HomeScreenFragment
import com.bcsd.android.lotteryticketapplication.view.view.mypageFragment.MyPageFragment
import com.bcsd.android.lotteryticketapplication.view.view.purchaseFragment.PurchaseFragment
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val myPageFragment = MyPageFragment()
    private val homeScreenFragment = HomeScreenFragment()
    private val purchaseFragment = PurchaseFragment()
    private val manager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.progressBar.visibility = View.VISIBLE

        manager
            .beginTransaction()
            .add(R.id.main_frame, myPageFragment)
            .add(R.id.main_frame, purchaseFragment)
            .add(R.id.main_frame, homeScreenFragment)
            .commit()

        createNavigation()

        val isRunningObserver = Observer<ArrayList<Boolean>>{
            if (false !in it){
                binding.progressBar.visibility = View.GONE
            }
        }
        mainViewModel.isRunning.observe(this, isRunningObserver)

        mainViewModel.createRealtimeDatabase()
        mainViewModel.createRetrofit()
    }

    private fun createNavigation() {
        val navigation = binding.mainNavigation
        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_navigation -> {
                    manager
                        .beginTransaction()
                        .show(homeScreenFragment)
                        .hide(myPageFragment)
                        .hide(purchaseFragment)
                        .commit()
                }
                R.id.purchase_navigation -> {
                    manager
                        .beginTransaction()
                        .show(purchaseFragment)
                        .hide(homeScreenFragment)
                        .hide(myPageFragment)
                        .commit()
                }
                R.id.my_page_navigation -> {
                    manager
                        .beginTransaction()
                        .show(myPageFragment)
                        .hide(homeScreenFragment)
                        .hide(purchaseFragment)
                        .commit()
                }
            }
            true
        }
    }
}