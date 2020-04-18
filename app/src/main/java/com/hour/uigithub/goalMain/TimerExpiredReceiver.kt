package com.hour.uigithub.goalMain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hour.uigithub.util.NotificationUtil
import com.hour.uigithub.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
