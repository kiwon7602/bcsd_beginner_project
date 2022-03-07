package com.bcsd.android.lotteryticketapplication.view.view.homeFragment

import android.os.Bundle
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


        val currentDateObserver = Observer<String> {
            homeScreenViewModel.setCurrentUserLotteryNumbers(it)
        }
        mainViewModel.date.observe(viewLifecycleOwner, currentDateObserver)
    }

    override fun onStart() {
        super.onStart()
        binding.editText.setText("")
        homeScreenViewModel.updatePastAllUserLotteryNumbers("")
        homeScreenViewModel.updatePastDate("")
    }

    // 오늘의 당첨 번호
    private fun getTodayWinningNumber() {
        val lotteryNumbersObserver = Observer<ArrayList<Int>> { todayWinningNumbers ->
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
            val editTextDate = binding.editText.text.toString()
            if (editTextDate.isEmpty()){
                Toast.makeText(context, "날짜를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else {
                homeScreenViewModel.findPastLotteryNumbers(editTextDate)
                getAllPastUserNumber()
            }
            val pastDateObserver = Observer<String> {
                binding.pastDate.text = it
            }
            homeScreenViewModel.pastDate.observe(viewLifecycleOwner, pastDateObserver)
        }
        val pastWinningNumbersObserver = Observer<MutableList<Int?>> { pastWinningNum ->
            pastWinningNum.forEach {
                when (pastWinningNum.indexOf(it)){
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

    // 과거 회원들의 로또 번호 리사이클러 뷰
    private fun getAllPastUserNumber() {
        val pastLotteryNumbers = Observer<String> {
            if (it == null) {
                homeScreenViewModel.updatePastDate("번호가 없는 날짜입니다.")
                homeScreenViewModel.updatePastAllUserLotteryNumbers("")
                binding.pastCircleBall1.setText("")
                binding.pastCircleBall2.setText("")
                binding.pastCircleBall3.setText("")
                binding.pastCircleBall4.setText("")
                binding.pastCircleBall5.setText("")
                binding.pastCircleBall6.setText("")
                binding.pastCircleBall7.setText("")
            } else {
                val allUserNumberList = mainViewModel.createTwoDimensionalList(it)
                adapter = HomeScreenAdapter(allUserNumberList)
                binding.lotteryBallsRecyclerView.adapter = adapter
                binding.lotteryBallsRecyclerView.layoutManager = LinearLayoutManager(view?.context)
                adapter.setItemClickListener(object : HomeScreenAdapter.OnItemClickListener {
                    override fun onClick(v: View, position: Int, lottery: List<Int>) {
                        var count = 0
                        val pastWinningNumbers = homeScreenViewModel.pastWinningNumbers.value!!
                        lottery.forEach { number ->
                            if (number in pastWinningNumbers) {
                                count++
                            }
                        }
                        Toast.makeText(requireContext(), "$count 개 일치!", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
        homeScreenViewModel.pastAllUserLotteryNumbers.observe(
            viewLifecycleOwner,
            pastLotteryNumbers
        )
    }

    // 모든 회원들의 로또 번호
    private fun getAllCurrentUserNumber() {
        val allUserNumberObserver = Observer<String> {
            val allUserNumberList = mainViewModel.createTwoDimensionalList(it)
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

    // 오늘의 번호와 오늘의 회원 번호를 비교하여 n자리 수를 확인하는 함수
    private fun checkWinningRanking(
        allUserList: List<MutableList<Int>>,
        winningNumber: List<Int>
    ) {
        val winnerhistory = mutableListOf<Int>(0, 0, 0, 0, 0, 0, 0, 0)
        allUserList.forEach { listIt ->
            var rankingcount = 0
            listIt.forEach {
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
        binding.currentNum0.text = winnerhistory[0].toString()
        binding.currentNum1.text = winnerhistory[1].toString()
        binding.currentNum2.text = winnerhistory[2].toString()
        binding.currentNum3.text = winnerhistory[3].toString()
        binding.currentNum4.text = winnerhistory[4].toString()
        binding.currentNum5.text = winnerhistory[5].toString()
        binding.currentNum6.text = winnerhistory[6].toString()
        binding.currentNum7.text = winnerhistory[7].toString()

    }
}