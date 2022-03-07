package com.bcsd.android.lotteryticketapplication.view.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class HomeScreenViewModel : ViewModel() {
    private lateinit var databaseReference: DatabaseReference

    private val _currentAllUserLotteryNumbers = MutableLiveData<String>()
    val currentAllUserLotteryNumbers : LiveData<String> = _currentAllUserLotteryNumbers

    private val _pastAllUserLotteryNumbers = MutableLiveData<String>()
    val pastAllUserLotteryNumbers = _pastAllUserLotteryNumbers

    private val _pastWinningNumbers = MutableLiveData<MutableList<Int>>()
    val pastWinningNumbers = _pastWinningNumbers

    private val _pastDate = MutableLiveData<String>()
    val pastDate = _pastDate


    var pastwinningItems = mutableListOf<Int>()

    // 데이터베이스에 저장 된 현재 날짜의 모든 회원들의 로또 번호를 불러오는 함수
    fun currentUserData(date: String) {

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

    fun makeTwoDimensionalList(stringNumber: String): List<MutableList<Int>> {
        var count = 0
        var allusernumberlist = mutableListOf<MutableList<Int>>()
        var intList = mutableListOf<Int>()
        var itList = stringNumber.split(" ") as MutableList<String>
        itList.removeAt(itList.size - 1)
        itList.forEach {
            intList.add(it.toInt())
        }

        while (count != intList.size) {
            count += 1 // count 1씩 증가
            if ((count + 1) % 7 == 0) { // count(6,13...) + 1 => 7의 배수일 때
                val innerList = intList.slice((count - 6)..count)
                allusernumberlist.add(innerList as MutableList<Int>)
            }
        }
        for (i in 0..allusernumberlist.size - 1) {
            allusernumberlist[i].sort()
        }
        return allusernumberlist
    }

    fun pastWinningData(pastList: List<Int>) {
        pastwinningItems.clear()
        pastwinningItems.addAll(pastList)
        pastWinningNumbers.postValue(pastwinningItems)
    }

    fun pastDateUserDataMatching(editTextDate: String, context: Context?) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        if (editTextDate == "") {
            Toast.makeText(context, "날짜를 입력하세요.", Toast.LENGTH_SHORT).show()
        } else {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.child("LotteryNumbers")
                        .child(editTextDate)
                    if (value.exists()) {
                        val masterNumber = value.child("master").value.toString()
                        val customerNumber = value.child("customer").value.toString()
                        pastAllUserLotteryNumbers.postValue(customerNumber)
                        pastDate.postValue(editTextDate)

                        val pastNumberIntData = mutableListOf<Int>()
                        val pastNumberStrData = masterNumber.split(" ") as MutableList<String>
                        pastNumberStrData.removeAt(pastNumberStrData.size - 1)
                        pastNumberStrData.forEach {
                            pastNumberIntData.add(it.toInt())
                        }
                        pastNumberIntData.sort()

                        pastWinningData(pastNumberIntData)
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
    fun createStringToList(it: String): MutableList<Int> {
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
