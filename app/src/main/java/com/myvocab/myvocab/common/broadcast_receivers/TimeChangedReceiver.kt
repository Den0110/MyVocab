package com.myvocab.myvocab.common.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.myvocab.myvocab.common.fasttranslation.FastTranslationServiceStarter
import com.myvocab.myvocab.util.scheduleReminder

class TimeChangedReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "TimeChangedReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_TIME_CHANGED || intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
            Log.d(TAG, "Time changed")
            context.sendBroadcast(Intent(context, FastTranslationServiceStarter::class.java))
            scheduleReminder(context)
        }
    }

}