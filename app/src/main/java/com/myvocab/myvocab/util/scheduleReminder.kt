package com.myvocab.myvocab.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.myvocab.myvocab.common.broadcast_receivers.ReminderReceiver
import java.util.*

const val TAG = "ScheduleReminder"

fun scheduleReminder(context: Context) {
    val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent = Intent(context, ReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)

    val calendar = Calendar.getInstance()
    val now = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 18)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)

    // check whether the time is earlier than current time. If so, set it to tomorrow.
    // otherwise, all alarms for earlier time will fire
    if (calendar.before(now)) {
        calendar.add(Calendar.DATE, 1)
    }

    Log.d(TAG, "Set reminder at ${calendar.time}")
    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent)
}