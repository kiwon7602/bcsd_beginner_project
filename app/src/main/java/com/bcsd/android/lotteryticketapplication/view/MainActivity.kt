package com.bcsd.android.lotteryticketapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.view.homeFragment.HomeScreenFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    private val homescreenragment = HomeScreenFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.bottom_navigation_list)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home_screen -> showFragment(homescreenragment)
                R.id.menu_buy_lottery_ticket -> showFragment(homescreenragment)
                R.id.menu_user_screen -> showFragment(homescreenragment)
            }

            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}