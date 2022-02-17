package com.bcsd.android.lotteryticketapplication.view.view.purchaseFragment

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
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.FragmentPurchaseBinding
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PurchaseFragment : Fragment() {
    private lateinit var binding: FragmentPurchaseBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    var deductionMoney = 0
    var randnumber = String()

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

        binding.randomButton.setOnClickListener {
            var mySet = mutableSetOf<Int>()
            while (mySet.size <= 6){
                mySet.add((1..45).random())
            }
            mySet.forEach {
                randnumber += it.toString()
                randnumber += " "
            }
            if (deductionMoney <= 0){
                Toast.makeText(context, "No Money!",Toast.LENGTH_SHORT).show()
            }
            else {
                deductionMoney = deductionMoney - 5000
                mainViewModel.myLotteryNumbers.postValue(randnumber)
                mainViewModel.updateData("userLotteryNumbers", randnumber, view.context)
                mainViewModel.updateData("money", deductionMoney, view.context)
            }
        }
    }
    private fun createMyLotteryNumbers(){
        var myrandnumberlist = mutableListOf<MutableList<String>>()
        var count = 0

        val myLotteryNumbersObserver = Observer<String> {
            binding.text1.text = it
            randnumber = it
            if (it.isNotEmpty()) {
                val allList = it.split(" ") as MutableList<String>
                allList.removeAt(allList.size - 1)

                while (count != allList.size) {
                    count += 1
                    if ((count + 1) % 7 == 0) {
                        val innerList = allList.slice((count - 6)..count) as MutableList<String>
                        myrandnumberlist.add(innerList)
                    }
                }
                for (i in 0..myrandnumberlist.size - 1) {
                    val comparator: Comparator<String> = compareBy { it.toInt() }
                    myrandnumberlist[i].sortWith(comparator)
                }
            }
            binding.text2.text = myrandnumberlist.toString()
            mainViewModel.updateMyLotteryNumbers(myrandnumberlist)
        }
        mainViewModel.myLotteryNumbers.observe(viewLifecycleOwner, myLotteryNumbersObserver)
    }

    private fun updateObserveData(){
        val myLotteryNumbersListObserver = Observer<MutableList<MutableList<String>>> {
            binding.text3.text = it.toString()
        }
        val moneyObserver = Observer<Int>{
            deductionMoney = it
        }
        mainViewModel.myLotteryNumbersList.observe(viewLifecycleOwner, myLotteryNumbersListObserver)
        mainViewModel.money.observe(viewLifecycleOwner, moneyObserver)
    }
}