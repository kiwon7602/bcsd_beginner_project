package com.bcsd.android.lotteryticketapplication.view.service

import com.bcsd.android.lotteryticketapplication.view.model.LotteryNumber
import retrofit2.Call
import retrofit2.http.GET

// http url 뒷 부분을 얻어와서 http 내부 값 가져오는 서비스
interface MainService {
    @GET("bcsd/lotto")
    fun getLotteryNumber():Call<LotteryNumber>
}