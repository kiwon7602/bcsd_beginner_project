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

    val email = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val money = MutableLiveData<Int>()
    val date = MutableLiveData<String>()
    val myLotteryNumbersStr = MutableLiveData<String>()
    val myLotteryNumbers = MutableLiveData<MutableList<MutableList<Int>>>()
    val lotteryNumbers = MutableLiveData<ArrayList<Int>>()

    // retrofit2, database 데이터 받아올 때, 크기 2인 boolean(false, true) 타입 리스트
    val isRunning = MutableLiveData<ArrayList<Boolean>>()

    val lotteryItems = ArrayList<Int>()
    val myLotteryItems = mutableListOf<MutableList<Int>>()
    val isRunningItems: ArrayList<Boolean> = arrayListOf(false, false)

    // 나의 당첨 번호 삭제
    fun deleteMyLotteryNumbers(){
        myLotteryItems.clear()
        myLotteryNumbers.postValue(myLotteryItems)
    }

    // 당첨 번호 업데이트 함수 ( http 통신 완료 후 받아온 데이터(당첨 번호) )
    fun updateLotteryNumbers(lotteryList: ArrayList<Int>) {
        lotteryItems.clear()
        lotteryItems.addAll(lotteryList)
        lotteryItems.sort()
        lotteryNumbers.postValue(lotteryItems)
    }

    // 회원 별 로또 번호 업데이트 함수
    fun updateMyLotteryNumbers(myLotteryList: MutableList<MutableList<Int>>) {
        myLotteryItems.clear()
        myLotteryItems.addAll(myLotteryList)
        myLotteryNumbers.postValue(myLotteryItems)
    }

    // 해당날짜의 회원이 구매한 로또를 모두 모아 데이터베이스에 저장
    fun updateCurrentTimeLotteryNumbers(randomNumberStr:String){
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.child("LotteryNumbers")
                    .child(date.value.toString())
                if (value.child("customer").exists()) {
                    var newStr = value.child("customer").value.toString()
                    newStr += randomNumberStr
                    databaseReference.child("LotteryNumbers")
                        .child(date.value.toString())
                        .child("customer")
                        .setValue(newStr)
                } else {
                    databaseReference.child("LotteryNumbers")
                        .child(date.value.toString())
                        .child("customer")
                        .setValue(randomNumberStr)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // 서버와 리얼타임데이터베이스 호출 완료 시 값 변경 함수
    fun updateIsRunning(index: Int, value: Boolean) {
        isRunningItems.set(index, value)
        isRunning.postValue(isRunningItems)
    }

    // 데이터베이스 값 변경 업데이트 함수
    // 매개변수 : 경로 이름, 변경 할 값(타입 : Any), 사용하는 view context
    fun updateData(key: String, value: Any, context: Context?) {
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

    // 7개의 번호를 랜덤으로 생성하는 함수
    fun createRandomNumber():MutableSet<Int>{
        var randomSet = mutableSetOf<Int>()
        while (randomSet.size <= 6) {
            randomSet.add((1..45).random())
        }
        return randomSet
    }

    // 문자열을 받아 2차원 리스트로 변경하는 함수
    fun createTwoDimensionalList(number: String): MutableList<MutableList<Int>> {
        var count = 0
        var numberList = mutableListOf<MutableList<Int>>()
        if (number != ""){
            var listInt = mutableListOf<Int>()
            var listIt = number.split(" ") as MutableList<String>
            listIt.removeAt(listIt.size - 1)
            listIt.forEach {
                listInt.add(it.toInt())
            }

            while (count != listInt.size) {
                count += 1 // count 1씩 증가
                if ((count + 1) % 7 == 0) { // count(6,13...) + 1 => 7의 배수일 때
                    val innerList = listInt.slice((count - 6)..count)
                    numberList.add(innerList as MutableList<Int>)
                }
            }
            for (i in 0..numberList.size - 1) {
                numberList[i].sort()
            }
        }
        return numberList
    }


    // 파이어베이스 리얼타임데이터베이스 연동
    fun createRealtimeDatabase() {
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
                        myLotteryNumbersStr.postValue(userValue.value.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // Retrofit2 http 통신
    fun createRetrofit() {
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