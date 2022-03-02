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

// 로그인 액티비티
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    // Firebase 인증을 위한 객체
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        firebaseAuth = FirebaseAuth.getInstance()

        // 파이어베이스 Authentication 연동 시 연결 된 유저(즉, 이메일)가 있을 시 메인 액티비티로 전환 (로그인 유지)
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 로그인 버튼 클릭 시 이벤트
        binding.signInButton.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val userPwd = binding.userPassword.text.toString()
            if (userEmail == "") { // 이메일 입력이 없을 때 또는 둘 다 없을 때
                Toast.makeText(this, "이메일을 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userPwd == "") { // 이메일 입력은 있고, 패스워드 입력은 없을 때
                Toast.makeText(this, "패스워드를 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userEmail != "" && userPwd != "") { // 이메일과 패스워드가 모두 정상적으로 입력이 되었을 때
                // signIn -> 파이어베이스 Auth 등록
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPwd)
                    .addOnCompleteListener(this@SignInActivity) {
                        if (it.isSuccessful) { // 로그인이 성공적으로 되었을 때 (로그인 액티비티 -> 메인 액티비티)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else { // 로그인이 실패했을 때
                            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // 회원가입 버튼 클릭 시 이벤트 (로그인 액티비티 -> 회원가입 액티비티)
        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}