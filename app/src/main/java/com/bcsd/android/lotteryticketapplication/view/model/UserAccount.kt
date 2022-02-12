package com.bcsd.android.lotteryticketapplication.view.model

data class UserAccount(
    val idToken: String,
    val emailId: String,
    val password: String,
    val name: String,
    val money: Int,
    val userLotteryNumbers: String
)
