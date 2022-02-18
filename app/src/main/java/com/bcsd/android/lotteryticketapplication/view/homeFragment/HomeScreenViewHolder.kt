package com.bcsd.android.lotteryticketapplication.view.homeFragment

import android.content.ClipData
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.FragmentHomescreenMainBinding
import com.bcsd.android.lotteryticketapplication.view.model.TodayLotteryNumber
import org.w3c.dom.Text

class HomeScreenViewHolder(private val binding: FragmentHomescreenMainBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: TodayLotteryNumber){
    }
    var firstNumber: TextView = itemView.findViewById(R.id.)
    var secendNumber: TextView = itemView.findViewById(R.id.)

    }
/*
    var pastBall1: TextView = itemView.findViewById(R.id.past_circle_ball_1)
    var pastBall2: TextView = itemView.findViewById(R.id.past_circle_ball_2)
    var pastBall3: TextView = itemView.findViewById(R.id.past_circle_ball_3)
    var pastBall4: TextView = itemView.findViewById(R.id.past_circle_ball_4)
    var pastBall5: TextView = itemView.findViewById(R.id.past_circle_ball_5)
    var pastBall6: TextView = itemView.findViewById(R.id.past_circle_ball_6)
    var pastBall7: TextView = itemView.findViewById(R.id.past_circle_ball_7)
    var pastWinnerRank: TextView =itemView.findViewById(R.id.past_winner_rank_text)
    var pastWinnerNumber: TextView =itemView.findViewById(R.id.past_winner_number_text)
    var pastWinnerMoney: TextView = itemView.findViewById(R.id.past_winner_money_text)

}

 */