package com.bcsd.android.lotteryticketapplication.view.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    // Firebase 인증을 위한 객체
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val userPwd = binding.userPassword.text.toString()
            if (userEmail == "") {
                Toast.makeText(this, "이메일을 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userPwd == "") {
                Toast.makeText(this, "패스워드를 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userEmail != "" && userPwd != "") {
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPwd)
                    .addOnCompleteListener(this@SignInActivity) {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}