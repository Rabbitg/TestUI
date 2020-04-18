package com.hour.uigithub.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.hour.uigithub.R
import com.hour.uigithub.util.login
import com.hour.uigithub.util.toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edit_text_password
import kotlinx.android.synthetic.main.activity_login.text_email

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        button_sign_in.setOnClickListener {
            // trim() : 오른쪽 끝에 있는 공백을 없애는 역할.
            val email = text_email.text.toString().trim()
            val password = edit_text_password.text.toString().trim()

            if(email.isEmpty()){
                text_email.error = "이메일을 입력해주십시오."
                text_email.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                text_email.error = "이메일 형식이 올바르지 않습니다."
                text_email.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty() || password.length < 6){
                edit_text_password.error = "비밀번호를 확인 해 주십시오."
                edit_text_password.requestFocus()
                return@setOnClickListener
            }
            loginUser(email,password)

        }

        text_view_register.setOnClickListener {
            // 회원가입 창으로 이동
            startActivity(Intent(this@LoginActivity,
                RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        // 파이어베이스에서 데이터 처리하는 시간이 걸리기 때문에 이때 프로그래스바 보이도록 한다.
        progressbar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task->
                progressbar.visibility = View.GONE
                if (task.isSuccessful){
                    //Helper 참조
                    login()
                }else{
                    task.exception?.message?.let{
                        toast(it)
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        // 현재유저이면?
        mAuth.currentUser?.let {
            login() //helper에서 login 함수호출
        }
    }
}
