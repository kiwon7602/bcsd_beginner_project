package com.bcsd.android.lotteryticketapplication.view.view.mypageFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.FragmentMyPageBinding
import com.bcsd.android.lotteryticketapplication.view.view.MyWinningActivity
import com.bcsd.android.lotteryticketapplication.view.view.SignInActivity
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// 마이페이지 프래그먼트(내 정보 확인)
class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mainViewModel: MainViewModel
    private lateinit var databaseReference: DatabaseReference

    var rechargeMoney: Int = 0 // 유저 돈
    var winningNumbers = ArrayList<Int>() // 당첨 번호

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
            .get(MainViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_page, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateObserveData()

        // 예치금 5000원씩 추가
        binding.rechargeMoney.setOnClickListener {
            rechargeMoney += 5000
            mainViewModel.money.postValue(rechargeMoney)
            mainViewModel.updateData("money", rechargeMoney, context)
        }

        // 나의 당첨 내역확인 화면으로 이동
        binding.checkWinning.setOnClickListener {
            val intent = Intent(context, MyWinningActivity::class.java)
            intent.putExtra("winningNumbers", winningNumbers)
            intent.putExtra("winningDate", mainViewModel.date.value)
            intent.putExtra("myLotteryNumbers", mainViewModel.myLotteryNumbersStr.value)
            startActivity(intent)
        }

        // 로그아웃
        binding.signOutButton.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // 회원탈퇴
        binding.deleteAccountButton.setOnClickListener {
            // 해당 회원의 정보를 담는 database 삭제
            databaseReference = FirebaseDatabase.getInstance().getReference("User")
            databaseReference.child("UserAccount").child(firebaseAuth.currentUser?.uid.toString())
                .setValue(null)
            // 해당 회원의 Authentication 삭제
            firebaseAuth.currentUser?.delete()
            firebaseAuth.signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // 해당 화면 새로고침
        val isRunningObserver = Observer<ArrayList<Boolean>> {
            if (false !in it) {
                binding.swipeRefresh.isRefreshing = false
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.createRealtimeDatabase()
            mainViewModel.createRetrofit()
            mainViewModel.isRunning.observe(viewLifecycleOwner, isRunningObserver)
        }
    }

    private fun updateObserveData() {
        val emailObserver = Observer<String> {
            binding.pvtEmail.text = it.toString()
        }
        val nameObserver = Observer<String> {
            binding.pvtName.text = it.toString()
        }
        val moneyObserver = Observer<Int> {
            binding.pvtMoney.text = it.toString()
            rechargeMoney = it
        }
        val lotteryNumbersObserver = Observer<ArrayList<Int>> {
            winningNumbers = it
        }

        mainViewModel.email.observe(viewLifecycleOwner, emailObserver)
        mainViewModel.name.observe(viewLifecycleOwner, nameObserver)
        mainViewModel.money.observe(viewLifecycleOwner, moneyObserver)
        mainViewModel.lotteryNumbers.observe(viewLifecycleOwner, lotteryNumbersObserver)
    }

}