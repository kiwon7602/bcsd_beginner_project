package com.bcsd.android.lotteryticketapplication.view.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class HomeScreenViewModel : ViewModel() {
    private lateinit var databaseReference: DatabaseReference

    val currentalluserlotterynumbers = MutableLiveData<String>()
    val pastalluserlotterynumbers = MutableLiveData<String>()
    val pastwinningnumbers = MutableLiveData<MutableList<Int>>()
    val pastdate = MutableLiveData<String>()

    var pastwinningItems = mutableListOf<Int>()

    fun currentUserData(date: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.child("LotteryNumbers")
                    .child(date)
                if (value.child("customer").exists()) {
                    currentalluserlotterynumbers.postValue(value.child("customer").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun makeTwoDimensionalList(stringnumber: String): MutableList<MutableList<Int>> {
        var count = 0
        var allusernumberlist = mutableListOf<MutableList<Int>>()
        var int_list = mutableListOf<Int>()
        var it_list = stringnumber.split(" ") as MutableList<String>
        it_list.removeAt(it_list.size - 1)
        it_list.forEach {
            int_list.add(it.toInt())
        }

        while (count != int_list.size) {
            count += 1 // count 1씩 증가
            if ((count + 1) % 7 == 0) { // count(6,13...) + 1 => 7의 배수일 때
                val innerList = int_list.slice((count - 6)..count)
                allusernumberlist.add(innerList as MutableList<Int>)
            }
        }
        for (i in 0..allusernumberlist.size - 1) {
            allusernumberlist[i].sort()
        }
        return allusernumberlist
    }

    fun pastWinningData(pastlist: MutableList<Int>) {
        pastwinningItems.clear()
        pastwinningItems.addAll(pastlist)
        pastwinningnumbers.postValue(pastwinningItems)
    }

    fun pastDateUserDataMatching(edittextdate: String, context: Context?) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        if (edittextdate == "") {
            Toast.makeText(context, "날짜를 입력하세요.", Toast.LENGTH_SHORT).show()
        } else {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.child("LotteryNumbers")
                        .child(edittextdate)
                    if (value.exists()) {
                        var masternumber = value.child("master").value.toString()
                        var customernumber = value.child("customer").value.toString()
                        pastalluserlotterynumbers.postValue(customernumber)
                        pastdate.postValue(edittextdate)

                        var int_list = mutableListOf<Int>()
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