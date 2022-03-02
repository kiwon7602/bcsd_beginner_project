package com.bcsd.android.lotteryticketapplication.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.ActivitySignUpBinding
import com.bcsd.android.lotteryticketapplication.view.model.LotteryNumber
import com.bcsd.android.lotteryticketapplication.view.model.UserAccount
import com.bcsd.android.lotteryticketapplication.view.service.MainService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 회원가입 액티비티
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
        // firebase RealtimeDatabase 참조 경로
        databaseReference = FirebaseDatabase.getInstance().getReference("User")

        // 회원가입 버튼 클릭 시 이벤트
        binding.signUpButton.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val userPassword = binding.userPassword.text.toString()
            val userName = binding.userName.text.toString()
            if (userEmail == "") { // 이메일 미입력 또는 이메일, 패스워드 미입력 시
                Toast.makeText(this, "이메일을 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userPassword == "") { // 패스워드 미입력 시
                Toast.makeText(this, "패스워드를 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userName == "") { // 이름 미입력 시
                Toast.makeText(this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show()
            } else if (userEmail != "" && userPassword != "" && userName != "") { // 이메일, 패스워드, 이름 입력 완료 시
                // 함수 호출
                registerSuccess(userEmail, userPassword, userName)
            }
        }
    }

    // 회원가입이 성공/실패 시 호출되는 함수
    private fun registerSuccess(email: String, password: String, name: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@SignUpActivity) {
                if (it.isSuccessful) { // 회원가입 성공 시 호출 (반례 : firebase currentuser id가 동일하면 실패)
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    // UserAccount 데이터 클래스 사용
                    userAccount = UserAccount(
                        idToken = firebaseUser?.uid.toString(),
                        emailId = firebaseUser?.email.toString(),
                        password = password,
                        name = name,
                        0,
                        ""
                    )

                    // Firebase RealtimeDatabase 에 User 별 데이터 저장
                    databaseReference.child("UserAccount").child(firebaseUser?.uid.toString())
                        .setValue(userAccount)

                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    // 로그인 유지를 위한 Auth 로그아웃
                    firebaseAuth.signOut()
                    finish()
                } else { // 회원가입 실패 시 호출
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

}