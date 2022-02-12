package com.bcsd.android.lotteryticketapplication.view.view.homeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.FragmentHomescreenMainBinding

class HomeScreenFragment : Fragment() {
    private lateinit var binding: FragmentHomescreenMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_homescreen_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setNumberBackground(number: Int, textView: TextView) {
        when (number) {
            in 1..10 -> textView.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_yellow) }
            in 11..20 -> textView.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_blue) }
            in 21..30 -> textView.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_red) }
            in 31..40 -> textView.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_purple) }
        }
        true
    }

}