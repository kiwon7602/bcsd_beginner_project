package com.bcsd.android.lotteryticketapplication.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bcsd.android.lotteryticketapplication.view.model.LotteryNumber
import com.bcsd.android.lotteryticketapplication.view.service.MainService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {
    private lateinit var databaseReference: DatabaseReference

    val email= MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val money= MutableLiveData<Int>()
    val myLotteryNumbers = MutableLiveData<String>()
    val lotteryNumbers = MutableLiveData<ArrayList<Int>>()
    val date = MutableLiveData<String>()
//    val lotteryNumber: MutableLiveData<ArrayList<Int>> by lazy {
//        MutableLiveData()
//    }

    val isRunning = MutableLiveData<ArrayList<Boolean>>()
    val lotteryItems = ArrayList<Int>()
    val isRunningItems : ArrayList<Boolean> = arrayListOf(false,false)

    fun updateLottoNumbers(lotteryList: ArrayList<Int>) { // 로또 당첨 번호 업데이트 함수
        lotteryItems.clear()
        lotteryItems.addAll(lotteryList)
        lotteryItems.sort()
        lotteryNumbers.postValue(lotteryItems)
    }

    fun updateIsRunning(index:Int, value : Boolean){ // 서버와 리얼타임데이터베이스 호출 완료 시 값 변경 함수
        isRunningItems.set(index, value)
        isRunning.postValue(isRunningItems)
    }

    fun createRealtimeDatabase(){ // 파이어베이스 리얼타임데이터베이스 연동
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                updateIsRunning(0,true)
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val a = snapshot.child("UserAccount").child(firebaseUser?.uid.toString())
                for (i in a.children) {
                    if (i.key == "emailId") {
                        email.postValue(i.value.toString())
                    }
                    if (i.key == "name") {
                        name.postValue(i.value.toString())
                    }
                    if (i.key == "money"){
                        money.postValue(i.value.toString().toInt())
                    }
                    if (i.key == "userLotteryNumbers"){
                        myLotteryNumbers.postValue(i.value.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun createRetrofit(){ // Retrofit2 http 통신
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api2.ysmstudio.be/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(MainService::class.java)
        service.getLotteryNumber().enqueue(object : Callback<LotteryNumber> {
            override fun onResponse(call: Call<LotteryNumber>, response: Response<LotteryNumber>) {
                if (response.isSuccessful){
                    updateIsRunning(1,true)
                    val result: LotteryNumber? = response.body()
                    updateLottoNumbers(result?.lottoNumbers!!)
                    date.postValue(result?.date)
                }
            }

            override fun onFailure(call: Call<LotteryNumber>, t: Throwable) {
            }
        })
    }
}