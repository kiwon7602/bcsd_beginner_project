package com.bcsd.android.lotteryticketapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcsd.android.lotteryticketapplication.R

class MyWinningAdapter(
    val myLotteryList: MutableList<MutableList<String>>
) : RecyclerView.Adapter<MyWinningAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myLotteryCount = itemView.findViewById<TextView>(R.id.text_1)
        fun bind(pos: Int) {
            myLotteryCount.text = (pos+1).toString()
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int, lottery:MutableList<String>)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_my_winning, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position, myLotteryList[position])
        }
    }

    override fun getItemCount(): Int {
        return myLotteryList.size
    }
}
