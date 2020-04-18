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
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        button_register.setOnClickListener {
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
                edit_text_password.error = "비밀번호는 6글자 이상이어야 합니다."
                edit_text_password.requestFocus()
                return@setOnClickListener
            }

            registerUser(email,password)

        }


        text_view_login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    private fun registerUser(email: String, password: String) {
        progressbar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task ->
                progressbar.visibility = View.GONE
                if(task.isSuccessful){
                    // 등록 성공
                    //Helper 참조
                    login()
                }else{
                    // 에러가 왜 떴는지 Helper.kt 에 message 를 띄움
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
