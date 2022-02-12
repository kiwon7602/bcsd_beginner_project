package com.bcsd.android.lotteryticketapplication.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.ActivityMyWinningBinding

class MyWinningActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyWinningBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_winning)

        var winningNumbers = intent.getIntegerArrayListExtra("winningNumbers")

        binding.text1.text = winningNumbers.toString()

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}