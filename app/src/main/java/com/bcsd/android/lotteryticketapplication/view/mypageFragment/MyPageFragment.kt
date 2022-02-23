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

        // 예치금 충전 버튼 클릭 시 이벤트
        binding.rechargeMoney.setOnClickListener {
            // 클릭 시 추가 돈 5000원
            rechargeMoney += 5000
            // viewModel 변경 된 money 값 저장
            mainViewModel.money.postValue(rechargeMoney)
            // 변경 된 money 값을 데이터베이스에 업데이트
            mainViewModel.updateData("money", rechargeMoney, view.context)
        }

        // 나의 당첨 내역 확인 버튼 클릭 시 이벤트 (마이페이지 -> 나의 당첨 내역 확인 액티비티)
        binding.checkWinning.setOnClickListener {
            val intent = Intent(context, MyWinningActivity::class.java)
            // 값 이동 : 당첨 번호, 당첨 날짜, 나의 당첨 번호
            intent.putExtra("winningNumbers", winningNumbers)
            intent.putExtra("winningDate", mainViewModel.date.value)
            intent.putExtra("myLotteryNumbers", mainViewModel.myLotteryNumbers.value)
            startActivity(intent)
        }

        // 로그아웃 버튼 클릭 시 이벤트
        binding.signOutButton.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish() // 해당 프래그먼트를 담고 있는 액티비티 종료
        }

        // 회원탈퇴 버튼 클릭 시 이벤트
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

        // 호출 중인 데이터 관찰(observe)
        val isRunningObserver = Observer<ArrayList<Boolean>> {
            if (false !in it) {
                // 호출 한 데이터가 정상일 때 (즉, false가 없을 때) isRefreshing false
                binding.swipeRefresh.isRefreshing = false
            }
        }
        // swipe refresh(새로고침 화면 아래로 스와이프 시 실행)
        binding.swipeRefresh.setOnRefreshListener {
            // retrofit, database 다시 호출
            mainViewModel.createRealtimeDatabase()
            mainViewModel.createRetrofit()
            // 호출하는 데이터가 정상적으로 오는 지 확인
            mainViewModel.isRunning.observe(viewLifecycleOwner, isRunningObserver)
        }
    }

    // viewModel 관찰(observer) 하는 함수
    private fun updateObserveData() {
        val emailObserver = Observer<String> {
            binding.pvtEmail.text = it.toString()
        }
        val nameObserver = Observer<String> {
            binding.pvtName.text = it.toString()
        }
        val moneyObserver = Observer<Int> {
            binding.pvtMoney.text = it.toString()
            // 유저의 돈 변동을 위해서 계속 관찰 (돈 충전 관련), rechargeMoney 변수로 사용
            rechargeMoney = it
        }
        val lotteryNumbersObserver = Observer<ArrayList<Int>> {
            // 당첨 번호를 저장한 데이터를 불러와 관찰, winningNumbers 변수로 사용
            winningNumbers = it
        }

        mainViewModel.email.observe(viewLifecycleOwner, emailObserver)
        mainViewModel.name.observe(viewLifecycleOwner, nameObserver)
        mainViewModel.money.observe(viewLifecycleOwner, moneyObserver)
        mainViewModel.lotteryNumbers.observe(viewLifecycleOwner, lotteryNumbersObserver)
    }

}