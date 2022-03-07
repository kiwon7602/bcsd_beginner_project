package com.bcsd.android.lotteryticketapplication.view.view.homeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        getTodayWinningNumber()
        getPastWinningNumber()
        getAllCurrentUserNumber()
        getAllPastUserNumber()

        val currentDateObserver = Observer<String> {
            //homeScreenViewModel.setCurrentUserLotteryNumbers(it)
            // 머지 과정에서 소실된 것으로 추정되어 추후 추가될 기능
        }
        mainViewModel.date.observe(viewLifecycleOwner, currentDateObserver)
    }

    // 오늘의 당첨 번호
    private fun getTodayWinningNumber() {
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

    // 과거 당첨 번호
    private fun getPastWinningNumber() {
        binding.pastVisibleButton.setOnClickListener {
            val editTextDate = binding.getDataEditText.text.toString()
            // homeScreenViewModel.findPastLotteryNumbers(editTextDate, requireContext())
            // 머지 과정에서 소실된 것으로 추정되어 추후 추가될 기능
            val pastDateObserver = Observer<String> {
                binding.pastDateText.text = it
            }
            homeScreenViewModel.pastDate.observe(viewLifecycleOwner, pastDateObserver)
        }
        val pastWinningNumbersObserver = Observer<MutableList<Int>> { PastListIt ->

            PastListIt.forEach {
                when (PastListIt.indexOf(it)) {
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
        homeScreenViewModel.pastWinningNumbers.observe(
            viewLifecycleOwner,
            pastWinningNumbersObserver
        )
    }

    private fun getAllPastUserNumber() {
        val pastLotteryNumbers = Observer<String> {
            val allUserNumberList = homeScreenViewModel.makeTwoDimensionalList(it)
            adapter = HomeScreenAdapter(allUserNumberList)
            binding.lotteryBallsRecyclerView.adapter = adapter
            binding.lotteryBallsRecyclerView.layoutManager = LinearLayoutManager(view?.context)
            adapter.setItemClickListener(object : HomeScreenAdapter.OnItemClickListener {
                override fun onClick(v: View, position: Int, lottery: List<Int>) {
                    var count = 0
                    val pastNumbers = Observer<MutableList<Int>> {
                        count = 0
                        lottery.forEach { number ->
                            if (number in it) {
                                count++
                            }
                        }
                    }
                    Toast.makeText(requireContext(), "$count 개 일치!", Toast.LENGTH_SHORT).show()
                    homeScreenViewModel.pastWinningNumbers.observe(viewLifecycleOwner, pastNumbers)

                }
            })
        }
        homeScreenViewModel.pastAllUserLotteryNumbers.observe(
            viewLifecycleOwner,
            pastLotteryNumbers
        )
    }

    private fun getAllCurrentUserNumber() {
        val allUserNumberObserver = Observer<String> {
            val allUserNumberList = homeScreenViewModel.makeTwoDimensionalList(it)
            val winningNumbers = Observer<ArrayList<Int>> {
                checkWinningRanking(allUserNumberList, it)
            }
            mainViewModel.lotteryNumbers.observe(viewLifecycleOwner, winningNumbers)
        }
        homeScreenViewModel.currentAllUserLotteryNumbers.observe(
            viewLifecycleOwner,
            allUserNumberObserver
        )
    }

    private fun todayWinningNumber() {
        val lotteryNumbersObserver = Observer<MutableList<Int>> { todayWinningNumbers ->
            var todayWinningNumbers = todayWinningNumbers
            todayWinningNumbers.sort()
            todayWinningNumbers.forEach { winningNumbers ->
                when (todayWinningNumbers.indexOf(winningNumbers)) {
                    0 -> binding.todayCircleBall1.text = winningNumbers.toString()
                    1 -> binding.todayCircleBall2.text = winningNumbers.toString()
                    2 -> binding.todayCircleBall3.text = winningNumbers.toString()
                    3 -> binding.todayCircleBall4.text = winningNumbers.toString()
                    4 -> binding.todayCircleBall5.text = winningNumbers.toString()
                    5 -> binding.todayCircleBall6.text = winningNumbers.toString()
                    6 -> binding.todayCircleBall7.text = winningNumbers.toString()
                }
            }
        }
        mainViewModel.lotteryNumbers.observe(viewLifecycleOwner, lotteryNumbersObserver)
    }

    private fun checkWinningRanking(
        allUserList: List<MutableList<Int>>,
        winningNumber: ArrayList<Int>
    ) {
        val winnerHistory = mutableListOf<Int>(0, 0, 0, 0, 0, 0, 0, 0)
        var rankingCount = 0
        allUserList.forEach { user ->
            rankingCount = 0
            user.forEach {
                if (it in winningNumber)
                    rankingCount += 1
            }
            when (rankingCount) {
                0 -> winnerHistory[0] += 1
                1 -> winnerHistory[1] += 1
                2 -> winnerHistory[2] += 1
                3 -> winnerHistory[3] += 1
                4 -> winnerHistory[4] += 1
                5 -> winnerHistory[5] += 1
                6 -> winnerHistory[6] += 1
                7 -> winnerHistory[7] += 1
            }
        }
        winnerHistory.forEach {
            var historyIndex = winnerHistory.indexOf(it)
            when (historyIndex) {
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

}
