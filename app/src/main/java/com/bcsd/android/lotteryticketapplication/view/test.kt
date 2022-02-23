package com.bcsd.android.lotteryticketapplication.view

import kotlin.reflect.typeOf


fun main() {
    val a = "3 2 3 4 5 1 2 "

    println(a)
    println(a.split(" "))
    println(a)
    val b = mutableListOf<Int>(1,2,3)
    val c = arrayListOf<Int>(1,2,3)

    var a_list = a.split(" ") as MutableList<String>

    a_list.removeAt(a_list.size - 1)

    print(a_list)
    for (i in 0..a_list.size - 1) {
        println("${a_list[i].javaClass.name}")
    }
    val comparator : Comparator<String> = compareBy { it.toInt() }
    a_list.sortWith(comparator)
    println(a_list)

    println("${a_list[0].javaClass}")


//    val a = mutableListOf(mutableListOf(1,2,3,4))
//    println(a)
//
//    for (i in a){
//        print(a[0])
//    }

//    val b = mutableListOf<MutableList<Int>>()
//    b.add(mutableListOf(1,2,3,4))
//    b.add(mutableListOf(1,2,3,4))
//    b.add(mutableListOf(4,3,6,1,5))
//    println(b.size)
//    println(b[2].sort())
//    print(b)

//    val c = mutableListOf<MutableList<String>>()
//    c.add(mutableListOf("12","23","4","14","26","6"))
//    println(c)
//    c[0].sort()
//    println(c)
}