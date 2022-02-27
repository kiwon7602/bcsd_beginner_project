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

// 메인 액티비티 (홈, 구매, 마이페이지 프래그먼트를 가지고 있는 액티비티)
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

        // retrofit2, database 데이터 불러올 때 progressbar 띄우기
        binding.progressBar.visibility = View.VISIBLE

        // fragment 내 상태유지를 위해서 replace -> add 사용
        manager
            .beginTransaction()
            .add(R.id.main_frame, myPageFragment)
            .add(R.id.main_frame, purchaseFragment)
            .add(R.id.main_frame, homeScreenFragment)
            .commit()

        // bottom navigation
        createNavigation()

        // retrofit2, database 데이터 관찰(observe)
        val isRunningObserver = Observer<ArrayList<Boolean>> {
            if (false !in it) { // 두 개 데이터 관찰 중(retrofit2,database) false 가 없을 시 progressbar is gone
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