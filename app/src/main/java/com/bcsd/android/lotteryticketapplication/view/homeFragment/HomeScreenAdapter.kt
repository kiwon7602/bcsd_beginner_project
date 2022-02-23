package com.bcsd.android.lotteryticketapplication.view.homeFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bcsd.android.lotteryticketapplication.databinding.FragmentHomescreenMainBinding
import com.bcsd.android.lotteryticketapplication.databinding.FragmentHomescreenRecyclerItemsBinding
import com.bcsd.android.lotteryticketapplication.view.model.TodayLotteryNumber
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel

class HomeScreenAdapter() : RecyclerView.Adapter<HomeScreenViewHolder>() {


    private val items = mutableListOf<TodayLotteryNumber>()//숫자 아이템 아이템들
    private val _items = mutableListOf<TodayLotteryNumber>()//개인 유저 정보 등수 , 당첨금
    private  lateinit var mainViewModel: MainViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeScreenViewHolder {
        val binding = FragmentHomescreenMainBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeScreenViewHolder(binding)
    }

    override fun onViewAttachedToWindow(holder: HomeScreenViewHolder) {
        super.onViewAttachedToWindow(holder)
                for(i in 0..6)
       _items[i].todayWinnerRank = mainViewModel.checkingNumber(i).toString()

    }



    override fun onBindViewHolder(holder: HomeScreenViewHolder, position: Int) {
        val currentItem = items[position] // 리사이클러뷰 아이템들 -> 뷰에 직접 대입
        val currentUserItem = _items[position]

        for( i in 0..6){//각각의 공대입
            holder.binding.todayCircleBall1.text = currentItem.todayBalls[i]

        }
        //결과 값 대입
        holder.binding.winnerText.text=currentUserItem.todayWinnerRank
        holder.binding.winnerMoney.text=currentUserItem.todayWinnerMoney



    }

    override fun getItemCount(): Int {
        return items.size
    }

}



/*
       val view =
           LayoutInflater.from(parent.context)
               .inflate(R.layout.fragment_homescreen_recycler_items, parent, false)
       return HomeScreenViewHolder(view)

       */