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

        var myLotteryNumbersList = createMyLotteryNumbers(myLotteryNumbers.toString())
        createAdapter(myLotteryNumbersList)
        clickView(winningNumbers as ArrayList<Int>)

        binding.textDate.text = winningDate
        binding.textLotteryNumbers.text = winningNumbers.toString()
    }

    // 나의 로또 번호 문자열을 2차원 리스트로 변경하는 함수
    private fun createMyLotteryNumbers(myLotteryNumbers: String): MutableList<MutableList<Int>> {
        // 나의 로또 번호를 담을 변경 가능 한 리스트(정수형)
        var myrandnumberlist = mutableListOf<MutableList<Int>>()
        // 로또 번호 개수를 7개까지이므로 제한 할 카운트 변수
        var count = 0

        if (myLotteryNumbers?.isNotEmpty() == true) {
            // 나의 로또 번호를 정수형으로 저장 할 변경 가능 한 리스트
            var int_list = mutableListOf<Int>()
            // split을 통해 나의 문자열 공백 자르고 mutablelist로 전환
            val str_list = myLotteryNumbers.split(" ") as MutableList<String>
            // 마지막 공백 "" 삭제
            str_list.removeAt(str_list.size - 1)
            str_list.forEach {
                int_list.add(it.toInt())
            }

            while (count != int_list.size) {
                count += 1
                if ((count + 1) % 7 == 0) {
                    val innerList = int_list.slice((count - 6)..count)
                    myrandnumberlist.add(innerList as MutableList<Int>)
                }
            }
            for (i in 0..myrandnumberlist.size - 1) {
                myrandnumberlist[i].sort()
            }
        }
        return myrandnumberlist
    }

    // 나의 로또 번호 2차원 리스트를 어뎁터를 통해서 리사이클러 뷰에 연결
    private fun createAdapter(myrandnumberlist: MutableList<MutableList<Int>>) {
        adapter = MyWinningAdapter(myrandnumberlist)
        binding.myWinningRecyclerView.adapter = adapter
        binding.myWinningRecyclerView.layoutManager = GridLayoutManager(this, 3)
    }

    // 회원의 각 로또 번호를 클릭하여 확인할 수 있는 함수
    private fun clickView(winningNumbers: ArrayList<Int>) {
        adapter.setItemClickListener(object : MyWinningAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int, lottery: MutableList<Int>) {
                binding.textMyLotteryNumbers.text = lottery.toString()
                // 클릭 시 해당 회원의 로또 번호를 저장하는 리스트
                var viewLotteryNumbers = arrayListOf<Int>()
                // 회원 로또 번호와 당첨 번호를 비교하여 같은 번호가 있을 시 저장 할 리스트
                var count_list = arrayListOf<Int>()

                // 회원의 로또 번호를 클릭 시 내부의 값을 viewLotteryNumbers 리스트에 저장
                lottery.forEach {
                    viewLotteryNumbers.add(it)
                }

                // 당첨 번호 내에 나의 로또 번호를 저장 리스트와 비교하여 같은면 count_list에 저장
                viewLotteryNumbers.forEach {
                    if (it in winningNumbers!!) {
                        count_list.add(it)
                    }
                }

                // count_list -> 당첨 번호와 같은 번호가 들어있는 리스트
                // count_list.size -> 몇개의 번호가 일치하는지 확인하는 변수
                binding.textSameNumberList.text = count_list.toString()
                binding.textSameNumber.text = count_list.size.toString()
            }
        })
    }
}