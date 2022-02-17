package com.bcsd.android.lotteryticketapplication.view.homeFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bcsd.android.lotteryticketapplication.R

class HomeScreenAdapter : RecyclerView.Adapter<HomeScreenViewHolder>() {

    private val items = mutableListOf<HomeScreenViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeScreenViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_homescreen_recycler_items, parent, false)
        return HomeScreenViewHolder(view)
    }


    override fun onBindViewHolder(holder: HomeScreenViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return items.size
    }
}