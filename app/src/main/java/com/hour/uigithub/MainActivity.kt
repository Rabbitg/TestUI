package com.hour.uigithub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.goal_dialog.*
import kotlinx.android.synthetic.main.goal_dialog.view.*
import kotlinx.android.synthetic.main.goal_dialog.view.dialogTitleEt

class MainActivity : AppCompatActivity() {

    // Firebase database 접근하기 위한 것
    lateinit var mDatabase: DatabaseReference

    private val database by lazy { FirebaseDatabase.getInstance() }

    companion object{
        val TAG = "MainActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase Instance 얻어오기 위한 것
        mDatabase = FirebaseDatabase.getInstance().reference


        // 추가 버튼 클릭했을 때 다이얼로그 보여주는 것
        imageView_add_click.setOnClickListener {
            addItemDialog()
        }


        val goalRef = database.getReference("Goal")

        goalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot?.value
                title_mainTextView.text = "$value"
                description_mainTextView.text = "$value"
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })

    }



    private fun addItemDialog() {

        // goal_dialog.xml 가져오는 곳
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.goal_dialog,null)

        // 다이얼로그 빌더
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("목표는 단 하나 !!")

        // 다이얼로그 보여주기
        val mAlertDialog = mBuilder.show()

        // 저장 버튼 눌렀을 시
        mDialogView.dialogSubmitBtn.setOnClickListener {

            if (mDialogView.dialogTitleEt.text.toString().isEmpty() || mDialogView.dialogDiscriptionEt.text.toString().isEmpty())
                {
                Toast.makeText(this, "모두 입력 해주셔야 합니다 !!", Toast.LENGTH_SHORT).show()
                }
            else
            {
                // Firebase 의 Goal 참조에서 객체를 저장하기 위한 새로운 키를 생성하고 참조를 newRef 에 저장
                val newRef = FirebaseDatabase.getInstance().getReference("Goal").push()
                // Custom Layout 의 EditText 들로부터 텍스트를 얻어온다.
                val title = mDialogView.dialogTitleEt.text.toString()
                val description = mDialogView.dialogDiscriptionEt.text.toString()
                // 목표Id 설정
                val goalId = newRef.push().key
                // Goal 객체 생성
                val goal = Goal(goalId)
                // 메인 텍스트뷰에 넘겨서 "목표" 만 보이도록 한다.
                title_mainTextView.text = title
                description_mainTextView.text = description
                // Goal 클래스에 제목, 목표 설명을 넘겨주어서 Database 에 전달
                goal.title = title_mainTextView.text.toString()
                goal.description = description_mainTextView.text.toString()
                newRef.setValue(goal)
                // 다이얼로그 사라지게 하는 것
                mAlertDialog.dismiss()
            }
        }
        // 취소 버튼 눌렀을 때
        mDialogView.dialogCancelBtn.setOnClickListener {
            // 다이얼로그 사라지게 하기
            mAlertDialog.dismiss()
        }

    }



}
