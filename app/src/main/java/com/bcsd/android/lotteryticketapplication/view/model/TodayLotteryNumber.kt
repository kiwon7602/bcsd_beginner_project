package com.bcsd.android.lotteryticketapplication.view.model

import android.widget.TextView
import com.bcsd.android.lotteryticketapplication.R

data class TodayLotteryNumber(
    val todayBall1: String,
    val todayBall2: String,
    val todayBall3: String,
    val todayBall4: String,
    val todayBall5: String,
    val todayBall6: String,
    val todayBall7: String,
    val todayWinnerRank: String,
    val todayWinnerNumber: String,
    val todayWinnerMoney: String
)
