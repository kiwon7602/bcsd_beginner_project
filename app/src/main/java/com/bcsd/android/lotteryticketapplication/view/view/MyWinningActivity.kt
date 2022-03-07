package com.bcsd.android.lotteryticketapplication.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.ActivityMyWinningBinding
import com.bcsd.android.lotteryticketapplication.view.adapter.MyWinningAdapter

// 나의 당첨내역 확인 액티비티
class MyWinningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyWinningBinding
    private lateinit var adapter: MyWinningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_winning)

        val winningNumbers = intent.getIntegerArrayListExtra("winningNumbers") // 당첨 번호
        val winningDate = intent.getStringExtra("winningDate") // 당첨 날짜
        val myLotteryNumbers = intent.getStringExtra("myLotteryNumbers") // 나의 로또 번호 문자열

        val myLotteryNumbersList = createMyLotteryNumbers(myLotteryNumbers.toString())
        createAdapter(myLotteryNumbersList)
        clickView(winningNumbers as ArrayList<Int>)

        binding.textDate.text = winningDate
        binding.textLotteryNumbers.text = winningNumbers.toString()
    }

    // 나의 로또 번호 문자열을 2차원 리스트로 변경하는 함수
    private fun createMyLotteryNumbers(myLotteryNumbers: String): List<MutableList<Int>> {
        val myRandNumberList = mutableListOf<MutableList<Int>>()
        var count = 0

        if (myLotteryNumbers?.isNotEmpty() == true) {
            var listInt = mutableListOf<Int>()
            val listStr = myLotteryNumbers.split(" ") as MutableList<String>
            listStr.removeAt(listStr.size - 1)
            listStr.forEach {
                listInt.add(it.toInt())
            }

            while (count != listInt.size) {
                count += 1
                if ((count + 1) % 7 == 0) {
                    val innerList = listInt.slice((count - 6)..count)
                    myRandNumberList.add(innerList as MutableList<Int>)
                }
            }
            for (i in 0..myRandNumberList.size - 1) {
                myRandNumberList[i].sort()
            }
        }
        return myRandNumberList
    }

    // 나의 로또 번호 2차원 리스트를 어뎁터를 통해서 리사이클러 뷰에 연결
    private fun createAdapter(myRandNumberList: List<MutableList<Int>>) {
        adapter = MyWinningAdapter(myRandNumberList)
        binding.myWinningRecyclerView.adapter = adapter
        binding.myWinningRecyclerView.layoutManager = GridLayoutManager(this, 3)
    }

    // 회원의 각 로또 번호를 클릭하여 확인할 수 있는 함수
    private fun clickView(winningNumbers: ArrayList<Int>) {
        adapter.setItemClickListener(object : MyWinningAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int, lottery: List<Int>) {
                binding.textMyLotteryNumbers.text = lottery.toString()
                val viewLotteryNumbers = arrayListOf<Int>()
                val listCount = arrayListOf<Int>()

                lottery.forEach {
                    viewLotteryNumbers.add(it)
                }

                viewLotteryNumbers.forEach {
                    if (it in winningNumbers!!) {
                        listCount.add(it)
                    }
                }

                binding.textSameNumberList.text = listCount.toString()
                binding.textSameNumber.text = listCount.size.toString()
            }
        })
    }
}