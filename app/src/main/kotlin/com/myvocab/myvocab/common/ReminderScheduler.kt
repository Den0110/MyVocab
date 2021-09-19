package com.myvocab.myvocab.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.myvocab.myvocab.BuildConfig
import com.myvocab.myvocab.common.broadcast_receivers.ReminderReceiver
import com.myvocab.myvocab.util.PreferencesManager
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
        }.timeInMillis
    }

    private val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, ReminderReceiver::class.java)
    private val pendingIntent = PendingIntent.getBroadcast(context, REMINDER_REQUEST_CODE, alarmIntent, 0)

    fun schedule() {
        schedule(prefManager.remindingTime)
    }

    fun schedule(remindingTime: Long) {
        val time = nextRemindTime(remindingTime)
        prefManager.remindingState = true
        prefManager.remindingTime = time
        Timber.d("Set reminder at ${Date(time)}")
        manager.cancel(pendingIntent)
        if(BuildConfig.EXACT_REMINDING) {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent)
        } else {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent)
        }
    }

    fun scheduleIfEnabled(){
        if(prefManager.remindingState)
            schedule()
    }

    fun scheduleIfEnabled(remindingTime: Long){
        if(prefManager.remindingState)
            schedule(remindingTime)
    }

    fun cancel() {
        prefManager.remindingState = false
        manager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun isReminderEnabled() = prefManager.remindingState

    fun isRemindOnlyWordsToLearn() = prefManager.remindOnlyWordsToLearn

    fun setRemindOnlyWordsToLearn(state: Boolean) { prefManager.remindOnlyWordsToLearn = state }

    fun getRemindingTime() = prefManager.remindingTime

}

fun nextRemindTime(time: Long): Long {
    val now = Calendar.getInstance()
    val cal = Calendar.getInstance().apply { timeInMillis = time }
    val inCal = Calendar.getInstance().apply { timeInMillis = time }

    cal.apply {
        set(Calendar.HOUR_OF_DAY, inCal.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, inCal.get(Calendar.MINUTE))
        set(Calendar.DATE, now.get(Calendar.DATE))
    }

    // check whether the time is earlier than current time. If so, set it to tomorrow.
    // otherwise, all alarms for earlier time will fire
    if(cal.before(now)){
        cal.add(Calendar.DATE, 1)
    }
    return cal.timeInMillis
}