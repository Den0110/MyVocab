package com.myvocab.myvocab.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.myvocab.myvocab.common.broadcast_receivers.ReminderReceiver
import com.myvocab.myvocab.util.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ReminderScheduler
@Inject
constructor(
        private val context: Context,
        private val prefManager: PreferencesManager
){

    companion object {
        private const val REMINDER_REQUEST_CODE = 911

        val REMINDER_DEFAULT_TIME = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            moveTimeToNextDayIfNeeded()
        }.timeInMillis
    }

    private val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, ReminderReceiver::class.java)
    private val pendingIntent = PendingIntent.getBroadcast(context, REMINDER_REQUEST_CODE, alarmIntent, 0)

    fun schedule() {
        schedule(prefManager.getRemindingTime())
    }

    fun schedule(remindingTime: Long) {
        prefManager.setRemindingState(true)
        prefManager.setRemindingTime(remindingTime)
        Timber.d("Set reminder at ${Date(remindingTime)}")
        manager.cancel(pendingIntent)
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, remindingTime, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun scheduleIfEnabled(){
        if(prefManager.getRemindingState())
            schedule()
    }

    fun scheduleIfEnabled(remindingTime: Long){
        if(prefManager.getRemindingState())
            schedule(remindingTime)
    }

    fun cancel() {
        prefManager.setRemindingState(false)
        manager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun isReminderEnabled() = prefManager.getRemindingState()

    fun isRemindOnlyWordsToLearn() = prefManager.getRemindOnlyWordsToLearn()

    fun setRemindOnlyWordsToLearn(state: Boolean) = prefManager.setRemindOnlyWordsToLearn(state)

    fun getRemindingTime() = prefManager.getRemindingTime()

}

fun Calendar.moveTimeToNextDayIfNeeded(){
    // check whether the time is earlier than current time. If so, set it to tomorrow.
    // otherwise, all alarms for earlier time will fire
    if(before(Calendar.getInstance())){
        add(Calendar.DATE, 1)
    }
}