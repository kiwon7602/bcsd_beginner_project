package com.bcsd.android.lotteryticketapplication.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.ActivitySignUpBinding
import com.bcsd.android.lotteryticketapplication.view.model.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userAccount: UserAccount
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        // firebaseAuth를 사용하기 위한 인스턴스 get
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("User")

        binding.signUpButton.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val userPassword = binding.userPassword.text.toString()
            val userName = binding.userName.text.toString()
            if (userEmail == "") {
                Toast.makeText(this, "이메일을 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userPassword == "") {
                Toast.makeText(this, "패스워드를 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userName == "") {
                Toast.makeText(this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userEmail != "" && userPassword != "" && userName != "") {
                registerSuccess(userEmail, userPassword, userName)
            }
        }
    }

    private fun registerSuccess(email: String, password: String, name: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@SignUpActivity) {
                if (it.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    userAccount = UserAccount(
                        idToken = firebaseUser?.uid.toString(),
                        emailId = firebaseUser?.email.toString(),
                        password = password,
                        name = name,
                        0,
                        ""
                        )

                    databaseReference.child("UserAccount").child(firebaseUser?.uid.toString())
                        .setValue(userAccount)

                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}