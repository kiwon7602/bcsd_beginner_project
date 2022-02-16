package com.bcsd.android.lotteryticketapplication.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.ActivityMyWinningBinding
import com.bcsd.android.lotteryticketapplication.view.adapter.MyWinningAdapter

class MyWinningActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyWinningBinding
    private lateinit var adapter:MyWinningAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_winning)


        val winningNumbers = intent.getIntegerArrayListExtra("winningNumbers")
        val winningDate = intent.getStringExtra("winningDate")
        val myLotteryNumbers = intent.getStringExtra("myLotteryNumbers")

        var myrandnumberlist = createMyLotteryNumbers(myLotteryNumbers.toString())
        createAdapter(myrandnumberlist)
        clickView(winningNumbers as ArrayList<Int>)

        binding.textDate.text = winningDate
        binding.textLotteryNumbers.text = winningNumbers.toString()

    }

    private fun createMyLotteryNumbers(myLotteryNumbers:String) :MutableList<MutableList<String>> {
        var myrandnumberlist = mutableListOf<MutableList<String>>()
        var count = 0

        if (myLotteryNumbers?.isNotEmpty() == true) {
            val allList = myLotteryNumbers.split(" ") as MutableList<String>
            allList.removeAt(allList.size - 1)

            while (count != allList.size) {
                count += 1
                if ((count + 1) % 6 == 0) {
                    val innerList = allList.slice((count - 5)..count) as MutableList<String>
                    myrandnumberlist.add(innerList)
                }
            }
            for (i in 0..myrandnumberlist.size - 1) {
                val comparator: Comparator<String> = compareBy { it.toInt() }
                myrandnumberlist[i].sortWith(comparator)
            }
        }
        return myrandnumberlist
    }

    private fun createAdapter(myrandnumberlist:MutableList<MutableList<String>>){
        adapter = MyWinningAdapter(myrandnumberlist)
        binding.myWinningRecyclerView.adapter = adapter
        binding.myWinningRecyclerView.layoutManager = GridLayoutManager(this,3)
    }

    private fun clickView(winningNumbers:ArrayList<Int>){
        adapter.setItemClickListener(object : MyWinningAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int, lottery: MutableList<String>) {
                binding.textMyLotteryNumbers.text = lottery.toString()
                var viewLotteryNumbers = arrayListOf<Int>()
                var samelist = arrayListOf<Int>()

                lottery.forEach {
                    viewLotteryNumbers.add(it.toInt())
                }

                viewLotteryNumbers.forEach {
                    if (it in winningNumbers!!){
                        samelist.add(it)
                    }
                }
                binding.textSameNumberList.text = samelist.toString()
                binding.textSameNumber.text = samelist.size.toString()
            }
        })
    }
}