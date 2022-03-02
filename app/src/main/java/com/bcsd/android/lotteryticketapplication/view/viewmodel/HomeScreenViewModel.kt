package com.bcsd.android.lotteryticketapplication.view.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class HomeScreenViewModel : ViewModel() {
    private lateinit var databaseReference: DatabaseReference

    val currentAllUserLotteryNumbers = MutableLiveData<String>()
    val pastAllUserLotteryNumbers = MutableLiveData<String>()
    val pastWinningNumbers = MutableLiveData<MutableList<Int>>()
    val pastDate = MutableLiveData<String>()

    var pastWinningItems = mutableListOf<Int>()

    // 데이터베이스에 저장 된 현재 날짜의 모든 회원들의 로또 번호를 불러오는 함수
    fun setCurrentUserLotteryNumbers(date: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.child("LotteryNumbers")
                    .child(date)
                if (value.child("customer").exists()) {
                    currentAllUserLotteryNumbers.postValue(value.child("customer").value.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // 과거 당첨 번호를 업데이트하는 함수
    fun updatePastWinningNumbers(pastlist: MutableList<Int>) {
        pastWinningItems.clear()
        pastWinningItems.addAll(pastlist)
        pastWinningNumbers.postValue(pastWinningItems)
    }

    // 과거 로또 번호가 존재하는 지 확인하는 함수
    fun findPastLotteryNumbers(editTextDate: String, context: Context?) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        if (editTextDate == "") {
            Toast.makeText(context, "날짜를 입력하세요.", Toast.LENGTH_SHORT).show()
        } else {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.child("LotteryNumbers")
                        .child(editTextDate)
                    val valueCustomer = snapshot.child("LotteryNumbers")
                        .child(editTextDate).child("customer")
                    if (valueCustomer.exists()){
                        var masterNumber = value.child("master").value.toString()
                        var customerNumber = value.child("customer").value.toString()
                        pastAllUserLotteryNumbers.postValue(customerNumber)
                        pastDate.postValue(editTextDate)

                        val listInt = createStringToList(masterNumber)

                        updatePastWinningNumbers(listInt)
                    } else {
                        Toast.makeText(context, "해당 날짜에 번호가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    // 문자열을 리스트로 변환하는 함수
    fun createStringToList(it:String) : MutableList<Int>{
        var listInt = mutableListOf<Int>()
        val listStr = it.split(" ") as MutableList<String>
        listStr.removeAt(listStr.size - 1)
        listStr.forEach {
            listInt.add(it.toInt())
        }
        listInt.sort()
        return listInt
    }
}