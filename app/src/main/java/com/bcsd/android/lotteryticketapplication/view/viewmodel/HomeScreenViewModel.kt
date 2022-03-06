package com.bcsd.android.lotteryticketapplication.view.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class HomeScreenViewModel : ViewModel() {
    private lateinit var databaseReference: DatabaseReference

    private val _currentAllUserLotteryNumbers = MutableLiveData<String>()
    val currentAllUserLotteryNumbers = _currentAllUserLotteryNumbers

    private val _pastAllUserLotteryNumbers = MutableLiveData<String>()
    val pastAllUserLotteryNumbers = _pastAllUserLotteryNumbers

    private val _pastWinningNumbers = MutableLiveData<MutableList<Int>>()
    val pastWinningNumbers = _pastWinningNumbers

    private val _pastDate = MutableLiveData<String>()
    val pastDate = _pastDate


    var pastwinningItems = mutableListOf<Int>()

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
                        val masternumber = value.child("master").value.toString()
                        val customernumber = value.child("customer").value.toString()
                        pastAllUserLotteryNumbers.postValue(customernumber)
                        pastDate.postValue(editTextDate)

                        val int_list = mutableListOf<Int>()
                        val str_list = masternumber.split(" ") as MutableList<String>
                        str_list.removeAt(str_list.size - 1)
                        str_list.forEach {
                            int_list.add(it.toInt())
                        }
                        int_list.sort()

                        pastWinningData(int_list)
                    } else {
                        Toast.makeText(context, "해당 날짜에 번호가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}