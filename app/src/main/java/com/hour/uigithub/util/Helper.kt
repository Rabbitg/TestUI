package com.hour.uigithub.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hour.uigithub.ui.HomeActivity
import com.hour.uigithub.ui.LoginActivity

fun Context.toast(message:String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.login(){
    //FLAG_ACTIIVTY_CLEAR_TASK는 기존에 쌓여있던
    //task(stack이 모여 형성하는 작업의 단위(?))를
    // 모두 삭제하는 조건(?)을 받는 flag 상수다.
    val intent = Intent(this, HomeActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}

fun Context.logout() {
    val intent = Intent(this, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}