package com.bcsd.android.lotteryticketapplication.view.view.homeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.FragmentHomescreenMainBinding
import com.bcsd.android.lotteryticketapplication.view.adapter.HomeScreenAdapter
import com.bcsd.android.lotteryticketapplication.view.viewmodel.HomeScreenViewModel
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeScreenFragment : Fragment() {
    private lateinit var binding: FragmentHomescreenMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mainViewModel: MainViewModel
    private lateinit var homeScreenViewModel: HomeScreenViewModel

    private lateinit var adapter: HomeScreenAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
            .get(MainViewModel::class.java)
        homeScreenViewModel =
            ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
                .get(HomeScreenViewModel::class.java)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_homescreen_main, container, false)
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayWinningNumber()
        pastWinningNumber()
        getAllCurrentUserNumber()
        getAllPastUserNumber()

        val getdate = Observer<String> {
            homeScreenViewModel.currentUserData(it)
        }
        mainViewModel.date.observe(viewLifecycleOwner, getdate)
    }

    private fun pastWinningNumber() {
        binding.pastVisibleButton.setOnClickListener {
            var edittextdate = binding.editText.text.toString()
            homeScreenViewModel.pastDateUserDataMatching(edittextdate, requireContext())
            val pastdateObserver = Observer<String> {
                binding.pastDate.text = it
            }
            homeScreenViewModel.pastdate.observe(viewLifecycleOwner, pastdateObserver)
        }
        val pastwinningnumbersObserver = Observer<MutableList<Int>> {
            val it_list = it
            it.forEach {
                when (it_list.indexOf(it)) {
                    0 -> binding.pastCircleBall1.text = it.toString()
                    1 -> binding.pastCircleBall2.text = it.toString()
                    2 -> binding.pastCircleBall3.text = it.toString()
                    3 -> binding.pastCircleBall4.text = it.toString()
                    4 -> binding.pastCircleBall5.text = it.toString()
                    5 -> binding.pastCircleBall6.text = it.toString()
                    6 -> binding.pastCircleBall7.text = it.toString()
                }
            }
        }
        homeScreenViewModel.pastwinningnumbers.observe(
            viewLifecycleOwner,
            pastwinningnumbersObserver
        )
    }

    private fun getAllPastUserNumber() {
        val pastlotterynumbers = Observer<String> {
            var allusernumberlist = homeScreenViewModel.makeTwoDimensionalList(it)
            adapter = HomeScreenAdapter(allusernumberlist)
            binding.lotteryBallsRecyclerView.adapter = adapter
            binding.lotteryBallsRecyclerView.layoutManager = LinearLayoutManager(view?.context)
            adapter.setItemClickListener(object : HomeScreenAdapter.OnItemClickListener {
                override fun onClick(v: View, position: Int, lottery: MutableList<Int>) {
                    var count = 0
                    val pastnumbers = Observer<MutableList<Int>> {
                        count = 0
                        lottery.forEach { number ->
                            if (number in it) {
                                count++
                            }
                        }
                    }
                    Toast.makeText(view?.context, "$count 개 일치!", Toast.LENGTH_SHORT).show()
                    homeScreenViewModel.pastwinningnumbers.observe(viewLifecycleOwner, pastnumbers)

                }
            })
        }
        homeScreenViewModel.pastalluserlotterynumbers.observe(
            viewLifecycleOwner,
            pastlotterynumbers
        )
    }

    private fun getAllCurrentUserNumber() {
        val allUserNumberObserver = Observer<String> {
            var allusernumberlist = homeScreenViewModel.makeTwoDimensionalList(it)
            val winningNumbers = Observer<ArrayList<Int>> {
                checkWinningRanking(allusernumberlist, it)
            }
            mainViewModel.lotteryNumbers.observe(viewLifecycleOwner, winningNumbers)
        }
        homeScreenViewModel.currentalluserlotterynumbers.observe(
            viewLifecycleOwner,
            allUserNumberObserver
        )
    }

    private fun todayWinningNumber() {
        val lotteryNumbersObserver = Observer<ArrayList<Int>> {
            var todayWinningNumbers = it
            todayWinningNumbers.sort()
            todayWinningNumbers.forEach {
                when (todayWinningNumbers.indexOf(it)) {
                    0 -> binding.todayCircleBall1.text = it.toString()
                    1 -> binding.todayCircleBall2.text = it.toString()
                    2 -> binding.todayCircleBall3.text = it.toString()
                    3 -> binding.todayCircleBall4.text = it.toString()
                    4 -> binding.todayCircleBall5.text = it.toString()
                    5 -> binding.todayCircleBall6.text = it.toString()
                    6 -> binding.todayCircleBall7.text = it.toString()
                }
            }
        }
        mainViewModel.lotteryNumbers.observe(viewLifecycleOwner, lotteryNumbersObserver)
    }

    private fun checkWinningRanking(
        alluserlist: MutableList<MutableList<Int>>,
        winningNumber: ArrayList<Int>
    ) {
        var winnerhistory = mutableListOf<Int>(0, 0, 0, 0, 0, 0, 0, 0)
        var rankingcount = 0
        alluserlist.forEach { listit ->
            rankingcount = 0
            listit.forEach {
                if (it in winningNumber)
                    rankingcount += 1
            }
            when (rankingcount) {
                0 -> winnerhistory[0] += 1
                1 -> winnerhistory[1] += 1
                2 -> winnerhistory[2] += 1
                3 -> winnerhistory[3] += 1
                4 -> winnerhistory[4] += 1
                5 -> winnerhistory[5] += 1
                6 -> winnerhistory[6] += 1
                7 -> winnerhistory[7] += 1
            }
        }
        winnerhistory.forEach {
            var _index = winnerhistory.indexOf(it)
            when (_index) {
                0 -> binding.currentNum0.text = it.toString()
                1 -> binding.currentNum1.text = it.toString()
                2 -> binding.currentNum2.text = it.toString()
                3 -> binding.currentNum3.text = it.toString()
                4 -> binding.currentNum4.text = it.toString()
                5 -> binding.currentNum5.text = it.toString()
                6 -> binding.currentNum6.text = it.toString()
                7 -> binding.currentNum7.text = it.toString()
            }
        }
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