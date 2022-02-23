package com.bcsd.android.lotteryticketapplication.view.model

import com.google.gson.annotations.SerializedName

// 당첨 로또 번호와 당첨 날짜를 가지고 있는 데이터 클래스
data class LotteryNumber(
    @SerializedName("lotto_numbers") var lottoNumbers:ArrayList<Int>,
    @SerializedName("date") var date:String
)
