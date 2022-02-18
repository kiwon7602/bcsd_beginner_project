package com.bcsd.android.lotteryticketapplication.view.model

import android.widget.TextView
import com.bcsd.android.lotteryticketapplication.R

data class TodayLotteryNumber(
    val todayBalls : ArrayList<String>,
    val todayWinnerRank: String,
    val todayWinnerNumber: String,
    val todayWinnerMoney: String
)
