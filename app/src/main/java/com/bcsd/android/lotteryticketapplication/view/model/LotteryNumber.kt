package com.bcsd.android.lotteryticketapplication.view.model

import com.google.gson.annotations.SerializedName


data class LotteryNumber(
    @SerializedName("lotto_numbers") var lottoNumbers:ArrayList<Int>,
    @SerializedName("date") var date:String
)
