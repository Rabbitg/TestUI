package com.hour.uigithub

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.hour.uigithub.util.NotificationUtil
import com.hour.uigithub.util.PrefUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_timer.*
import kotlinx.android.synthetic.main.goal_dialog.view.*
import java.util.*


class MainActivity : AppCompatActivity() {


    companion object{
        val TAG = "MainActivity"
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long{
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
        get() = Calendar.getInstance().timeInMillis/1000

    }
    // 카운트 다운 타이머
    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds : Long = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining : Long = 0L

    // Firebase database 접근하기 위한 것
    lateinit var mDatabase: DatabaseReference

    private val database by lazy { FirebaseDatabase.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "      Timer"
        // 카운트다운
        fab_start.setOnClickListener { v ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        fab_pause.setOnClickListener { v->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        fab_stop.setOnClickListener {
            timer.cancel()
            onTimerFinished()
        }


        // Firebase Instance 얻어오기 위한 것
        mDatabase = FirebaseDatabase.getInstance().reference


        // 추가 버튼 클릭했을 때 다이얼로그 보여주는 것
        imageView_add_click.setOnClickListener {
            addItemDialog()
        }

        val goalRef = database.getReference("/Goal")
        goalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach {
                    //"it" is the snapshot
                    val key: String = it.key.toString()
                    val title : Any? = dataSnapshot.child(key).child("title").value.toString()
                    title_mainTextView.text = title.toString()

                    val description : Any? = dataSnapshot.child(key).child("description").value.toString()
                    description_mainTextView.text = description.toString()
                }

                // val value = dataSnapshot.child("title").value



            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })

    }

    override fun onResume() {
        super.onResume()

        initTimer()

        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)

    }


    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        }
        else if (timerState == TimerState.Paused){
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)

        //we don't want to change the length of the timer which is already running
        //if the length was changed in settings while it was backgrounded
        if (timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if (secondsRemaining <= 0)
            onTimerFinished()
        else if (timerState == TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped

        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        setNewTimerLength()

        progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        textView_countdown.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons(){
        when (timerState) {
            TimerState.Running ->{
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.Stopped -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.Paused -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
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
                goal.writeTime = ServerValue.TIMESTAMP
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}
