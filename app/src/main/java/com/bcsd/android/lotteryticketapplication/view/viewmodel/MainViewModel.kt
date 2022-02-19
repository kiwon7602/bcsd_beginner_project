package com.bcsd.android.lotteryticketapplication.view.viewmodel

import android.content.Context
import android.widget.Toast
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
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api2.ysmstudio.be/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(MainService::class.java)

    private lateinit var databaseReference: DatabaseReference

    val email = MutableLiveData<String>() // 유저 이메일
    val name = MutableLiveData<String>() // 유저 이름
    val money = MutableLiveData<Int>() // 유저 예치금
    val myLotteryNumbers = MutableLiveData<String>() // 유저 로또 번호 문자열 (숫자 사이 공백, 데이터베이스 저장 시)
    val myLotteryNumbersList = MutableLiveData<MutableList<MutableList<Int>>>() // 유저 로또 번호 2차원 리스트(정수형)
    val lotteryNumbers = MutableLiveData<ArrayList<Int>>() // 당첨 번호
    val date = MutableLiveData<String>() // 당첨 날짜
    // retrofit2, database 데이터 받아올 때, 크기 2인 boolean(false, true) 타입 리스트
    val isRunning = MutableLiveData<ArrayList<Boolean>>()

    val lotteryItems = ArrayList<Int>() // 당첨 번호를 담고 있을 리스트
    val myLotteryItems = mutableListOf<MutableList<Int>>() // 유저 로또 번호를 담을 2차원 리스트(정수형)
    val isRunningItems: ArrayList<Boolean> = arrayListOf(false, false) // isRunning 라이브 데이터를 대신 할 리스트

    // 당첨 번호 업데이트 함수 ( http 통신 완료 후 받아온 데이터(당첨 번호) )
    fun updateLotteryNumbers(lotteryList: ArrayList<Int>) {
        lotteryItems.clear()
        lotteryItems.addAll(lotteryList)
        lotteryItems.sort()
        lotteryNumbers.postValue(lotteryItems)
    }

    fun updateMyLotteryNumbers(myLotteryList: MutableList<MutableList<Int>>) {
        myLotteryItems.clear()
        myLotteryItems.addAll(myLotteryList)
        myLotteryNumbersList.postValue(myLotteryList)
    }

    fun updateIsRunning(index: Int, value: Boolean) { // 서버와 리얼타임데이터베이스 호출 완료 시 값 변경 함수
        isRunningItems.set(index, value)
        isRunning.postValue(isRunningItems)
    }

    // 데이터베이스 값 변경 업데이트 함수
    // 매개변수 : 경로 이름, 변경 할 값(타입 : Any), 사용하는 view context
    fun updateData(key: String, value: Any, context: Context) {
        val firebaseAuth = FirebaseAuth.getInstance()
        // map 사용 -> {"1":"1","2":2...}
        var map = mutableMapOf<String, Any>()
        map[key] = value
        databaseReference.child("UserAccount").child(firebaseAuth.currentUser?.uid.toString())
            .updateChildren(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "데이터 업데이트 성공", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun createRealtimeDatabase() { // 파이어베이스 리얼타임데이터베이스 연동
        // databaseReference -> User 경로 참조
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                updateIsRunning(0, true)
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val account = snapshot.child("UserAccount").child(firebaseUser?.uid.toString())

                // 데이터 변동 시 viewModel 내부의 User 데이터 값 저장
                for (userValue in account.children) {
                    if (userValue.key == "emailId") {
                        email.postValue(userValue.value.toString())
                    }
                    if (userValue.key == "name") {
                        name.postValue(userValue.value.toString())
                    }
                    if (userValue.key == "money") {
                        money.postValue(userValue.value.toString().toInt())
                    }
                    if (userValue.key == "userLotteryNumbers") {
                        myLotteryNumbers.postValue(userValue.value.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun createRetrofit() { // Retrofit2 http 통신
        service.getLotteryNumber().enqueue(object : Callback<LotteryNumber> {
            override fun onResponse(call: Call<LotteryNumber>, response: Response<LotteryNumber>) {
                if (response.isSuccessful) { // 통신 성공 시
                    updateIsRunning(1, true)
                    val result: LotteryNumber? = response.body()
                    // 당첨 번호, 당첨 날짜 저장
                    updateLotteryNumbers(result?.lottoNumbers!!)
                    date.postValue(result?.date)

                    // 해당년도의 당첨 번호 불러와서 master 데이터베이스에 저장
                    // ex) 2022-02-19 -> master -> [해당년도 당첨 번호]
                    var date_lotterynumbers = String()
                    // 당첨 번호를 데이터베이스에 저장하기 위한 문자열 변환
                    // ex) [1,2,3,4,5,6,7] -> "1 2 3 4 5 6 7 " (문자 사이 구분하기위한 공백)
                    result?.lottoNumbers!!.forEach {
                        date_lotterynumbers += it.toString() + " "
                    }
                    // 데이터베이스 자식 중 master에 value 당첨번호 저장
                    databaseReference.child("LotteryNumbers")
                        .child(result?.date.toString()).child("master")
                        .setValue(date_lotterynumbers)
                }
            }
            // 통신 실패 시
            override fun onFailure(call: Call<LotteryNumber>, t: Throwable) {
            }
        })
    }
}