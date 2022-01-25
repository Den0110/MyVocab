package com.myvocab.fasttranslation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.myvocab.core.BuildConfig
import com.myvocab.core.util.PreferencesManager
import com.myvocab.domain.service.ReminderScheduler
import com.myvocab.fasttranslation.broadcast_receivers.ReminderReceiver
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ReminderSchedulerImpl
@Inject
constructor(
    private val context: Context,
    private val prefManager: PreferencesManager
) : ReminderScheduler {

    companion object {
        private const val REMINDER_REQUEST_CODE = 911
    }

    private val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, ReminderReceiver::class.java)
    private val pendingIntent = PendingIntent.getBroadcast(context, REMINDER_REQUEST_CODE, alarmIntent, 0)

    override fun schedule() {
        schedule(prefManager.remindingTime)
    }

    override fun schedule(remindingTime: Long) {
        val time = nextRemindTime(remindingTime)
        prefManager.remindingState = true
        prefManager.remindingTime = time
        Timber.d("Set reminder at ${Date(time)}")
        manager.cancel(pendingIntent)
        if (BuildConfig.EXACT_REMINDING) {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent)
        } else {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent)
        }
    }

    override fun scheduleIfEnabled() {
        if (prefManager.remindingState)
            schedule()
    }

    override fun scheduleIfEnabled(remindingTime: Long) {
        if (prefManager.remindingState)
            schedule(remindingTime)
    }

    override fun cancel() {
        prefManager.remindingState = false
        manager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun isReminderEnabled() = prefManager.remindingState

    override fun isRemindOnlyWordsToLearn() = prefManager.remindOnlyWordsToLearn

    override fun setRemindOnlyWordsToLearn(state: Boolean) {
        prefManager.remindOnlyWordsToLearn = state
    }

    override fun getRemindingTime() = prefManager.remindingTime

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
    if (cal.before(now)) {
        cal.add(Calendar.DATE, 1)
    }
    return cal.timeInMillis
}