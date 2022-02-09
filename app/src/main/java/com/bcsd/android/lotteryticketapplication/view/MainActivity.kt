package com.bcsd.android.lotteryticketapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.view.homeFragment.HomeScreenFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    private val  homescreenragment = HomeScreenFragment ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.)
    }
}