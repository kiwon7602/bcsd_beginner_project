package com.bcsd.android.lotteryticketapplication.view.view.mypageFragment

import android.content.Intent
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
import com.bcsd.android.lotteryticketapplication.databinding.FragmentMyPageBinding
import com.bcsd.android.lotteryticketapplication.view.view.MyWinningActivity
import com.bcsd.android.lotteryticketapplication.view.view.SignInActivity
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mainViewModel: MainViewModel
    private lateinit var databaseReference: DatabaseReference
    var rechargeMoney: Int = 0
    var winningNumbers = ArrayList<Int>()
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

        observerData()

        binding.rechargeButton.setOnClickListener {
            rechargeMoney += 5000
            mainViewModel.money.postValue(rechargeMoney)
            updataData(rechargeMoney)
        }

        binding.winningNumberButton.setOnClickListener {
            val intent = Intent(context, MyWinningActivity::class.java)
            intent.putExtra("winningNumbers",winningNumbers)
            startActivity(intent)
        }

        binding.signOutButton.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        binding.deleteAccountButton.setOnClickListener {
            firebaseAuth.currentUser?.delete()
            firebaseAuth.signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.createRealtimeDatabase()
            mainViewModel.createRetrofit()
            val isRunningObserver = Observer<ArrayList<Boolean>>{
                if (false !in it){
                    binding.swipeRefresh.isRefreshing = false
                }
            }
            mainViewModel.isRunning.observe(viewLifecycleOwner, isRunningObserver)
        }
    }

    private fun observerData(){
        val emailObserver = Observer<String> {
            binding.text1.text = it.toString()
        }
        val nameObserver = Observer<String> {
            binding.text2.text = it.toString()
        }
        val moneyObserver = Observer<Int> {
            binding.text3.text = it.toString()
            rechargeMoney = it
        }
        val lottoNumbersObserver = Observer<ArrayList<Int>>{
            binding.text4.text= it.toString()
            winningNumbers = it
        }
        val dateObserver = Observer<String>{
            binding.text5.text = it.toString()
        }

        mainViewModel.email.observe(viewLifecycleOwner, emailObserver)
        mainViewModel.name.observe(viewLifecycleOwner, nameObserver)
        mainViewModel.money.observe(viewLifecycleOwner, moneyObserver)
        mainViewModel.lotteryNumbers.observe(viewLifecycleOwner, lottoNumbersObserver)
        mainViewModel.date.observe(viewLifecycleOwner, dateObserver)
    }

    private fun updataData(rechargeMoney: Int) {
        var map = mutableMapOf<String, Any>()
        map["money"] = rechargeMoney
        databaseReference.child("UserAccount").child(firebaseAuth.currentUser?.uid.toString())
            .updateChildren(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "데이터 업데이트 성공", Toast.LENGTH_SHORT).show()
                }
            }
    }
}