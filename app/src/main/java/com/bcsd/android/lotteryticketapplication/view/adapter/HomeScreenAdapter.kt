package com.bcsd.android.lotteryticketapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcsd.android.lotteryticketapplication.R

class HomeScreenAdapter(
    val itemList : MutableList<MutableList<Int>>
) : RecyclerView.Adapter<HomeScreenAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ball1 = itemView.findViewById<TextView>(R.id.past_circle_ball_1)
        val ball2 = itemView.findViewById<TextView>(R.id.past_circle_ball_2)
        val ball3 = itemView.findViewById<TextView>(R.id.past_circle_ball_3)
        val ball4 = itemView.findViewById<TextView>(R.id.past_circle_ball_4)
        val ball5 = itemView.findViewById<TextView>(R.id.past_circle_ball_5)
        val ball6 = itemView.findViewById<TextView>(R.id.past_circle_ball_6)
        val ball7 = itemView.findViewById<TextView>(R.id.past_circle_ball_7)
        fun bind(innerlist:MutableList<Int>){
            ball1.text = innerlist[0].toString()
            ball2.text = innerlist[1].toString()
            ball3.text = innerlist[2].toString()
            ball4.text = innerlist[3].toString()
            ball5.text = innerlist[4].toString()
            ball6.text = innerlist[5].toString()
            ball7.text = innerlist[6].toString()
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int, lottery:MutableList<Int>)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_homescreen_recycler_items, parent, false )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position, itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}

