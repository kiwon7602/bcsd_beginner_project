package com.bcsd.android.lotteryticketapplication.view.service

import com.bcsd.android.lotteryticketapplication.view.model.LotteryNumber
import retrofit2.Call
import retrofit2.http.GET

interface MainService {
    @GET("bcsd/lotto")
    fun getLotteryNumber():Call<LotteryNumber>
}