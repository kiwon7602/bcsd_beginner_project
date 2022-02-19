package com.bcsd.android.lotteryticketapplication.view.view.purchaseFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.FragmentPurchaseBinding
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// 회원 로또 구매 프래그먼트 (로또 구매(자동))
class PurchaseFragment : Fragment() {
    private lateinit var binding: FragmentPurchaseBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    var deductionMoney = 0 // 로또 구매 시 회원 돈 절감
    var str_userLotteryNumbers = String() // 회원 로또 번호 저장 문자열

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
            .get(MainViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchase, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createMyLotteryNumbers()
        updateObserveData()

        // 로또 번호 구매 랜덤 버튼 클릭 시 이벤트
        binding.randomButton.setOnClickListener {
            var one_list = String() // 구매 번호 리스트 하나를 담을 문자열
            var mySet = mutableSetOf<Int>() // set을 사용한 중북제거
            while (mySet.size <= 6) {
                mySet.add((1..45).random())
            }
            // mySet 반복문으로 one_list 와 유저의 로또 리스트에 번호 값 저장
            mySet.forEach {
                one_list += it.toString()
                one_list += " "
                str_userLotteryNumbers += it.toString()
                str_userLotteryNumbers += " "
            }
            // 만약 현재 보유 금액이 0원 이하라면 진행 불가
            if (deductionMoney <= 0) {
                Toast.makeText(context, "No Money!", Toast.LENGTH_SHORT).show()
            } else { // 현재 보유 금액이 0원 보다 높으면 진행 가능
                deductionMoney = deductionMoney - 5000 // 로또 금액 5000원
                mainViewModel.myLotteryNumbers.postValue(str_userLotteryNumbers) // 나의 로또 번호 문자열 값 변경 저장
                mainViewModel.money.postValue(deductionMoney) // 나의 예치금 변경 저장
                // 나의 로또 번호와 예치금 변경 시 데이터베이스 update 실행
                mainViewModel.updateData("userLotteryNumbers", str_userLotteryNumbers, view.context)
                mainViewModel.updateData("money", deductionMoney, view.context)

                // 년도별 유저들이 구매한 로또 번호 데이터를 customer에 전체 저장
                // ex) LotteryNumbers -> 2022-02-20(해당년도) -> customer 존재 유무 파악 후 실행
                // addListenerForSingleValueEvent : 한 번 실행(데이터 한 번 불러오기)
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value = snapshot.child("LotteryNumbers")
                            .child(mainViewModel.date.value.toString())
                        if (value.child("customer").exists()) { // 해당년도의 로또를 구매 한 유저가 존재할 때
                            // 데이터 베이스에서 customer의 값을 문자열로 불러온다.
                            var new_userLotteryNumbers = value.child("customer").value.toString()
                            // 불러온 데이터를 위에서 랜덤 구매 버튼 클릭 시 얻어오는 하나의 리스트를 뒤에 저장한다.
                            new_userLotteryNumbers += one_list
                            // 해당 데이터를 저장한다.
                            databaseReference.child("LotteryNumbers")
                                .child(mainViewModel.date.value.toString())
                                .child("customer")
                                .setValue(new_userLotteryNumbers)
                        } else { // 해당년도의 로또를 구매 한 유저가 없을 때, 한 번 실행
                            databaseReference.child("LotteryNumbers")
                                .child(mainViewModel.date.value.toString())
                                .child("customer")
                                .setValue(str_userLotteryNumbers)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }
    }

    // 나의 로또 번호 문자열에서 리스트로 변환 함수
    private fun createMyLotteryNumbers() {
        // 나의 로또 번호를 담을 변경 가능 한 리스트(정수형)
        var myrandnumberlist = mutableListOf<MutableList<Int>>()
        // 로또 번호 개수를 7개까지이므로 제한 할 카운트 변수
        var count = 0

        val myLotteryNumbersObserver = Observer<String> {
            binding.text1.text = it
            // 나의 로또 번호 문자열 저장
            str_userLotteryNumbers = it
            // 나의 로또 번호 문자열이 빈 문자열이 아닐 때
            if (it.isNotEmpty()) {
                // 나의 로또 번호를 정수형으로 저장 할 변경 가능 한 리스트
                var int_list = mutableListOf<Int>()
                // split을 통해 나의 문자열 공백 자르고 mutablelist로 전환
                val str_list = it.split(" ") as MutableList<String>
                // 마지막 공백 "" 삭제
                str_list.removeAt(str_list.size - 1)
                // 리스트 내부의 타입을 문자열에서 정수형으로 전환 (split 시 강제 문자열 취급)
                // 7개 값을 가지는 리스트 생성
                str_list.forEach {
                    int_list.add(it.toInt())
                }

                // count와 int_list.size가 7로 같아질 때 break
                while (count != int_list.size) {
                    count += 1 // count 1씩 증가
                    if ((count + 1) % 7 == 0) { // count(6,13...) + 1 => 7의 배수일 때
                        // int_list에 있는 여러 개의 값을 0~6 (7개 씩) 슬라이스
                        val innerList = int_list.slice((count - 6)..count)
                        // 2차원 리스트에 나의 로또 번호 7개를 담은 리스트 저장
                        myrandnumberlist.add(innerList as MutableList<Int>)
                    }
                }
                // 리스트 하나 하나 정렬
                for (i in 0..myrandnumberlist.size - 1) {
                    myrandnumberlist[i].sort()
                }
            }
            binding.text2.text = myrandnumberlist.toString()
            // 나의 로또 번호 2차원 리스트 업데이트
            mainViewModel.updateMyLotteryNumbers(myrandnumberlist)
        }
        mainViewModel.myLotteryNumbers.observe(viewLifecycleOwner, myLotteryNumbersObserver)
    }

    // ViewModel 데이터 관찰(observe) 함수
    private fun updateObserveData() {
        // 회원 로또 번호 관찰(observe)
        val myLotteryNumbersListObserver = Observer<MutableList<MutableList<Int>>> {
            binding.text3.text = it.toString()
        }
        // 회원 돈 관찰(observe)
        val moneyObserver = Observer<Int> {
            deductionMoney = it
        }
        mainViewModel.myLotteryNumbersList.observe(viewLifecycleOwner, myLotteryNumbersListObserver)
        mainViewModel.money.observe(viewLifecycleOwner, moneyObserver)
    }
}