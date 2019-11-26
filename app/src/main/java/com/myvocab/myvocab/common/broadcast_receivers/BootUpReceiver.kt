package com.myvocab.myvocab.common.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.myvocab.myvocab.common.fasttranslation.FastTranslationServiceStarter
import com.myvocab.myvocab.util.scheduleReminder

class BootUpReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "BootUpReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Log.d(TAG, "Boot up")
            context.sendBroadcast(Intent(context, FastTranslationServiceStarter::class.java))
            scheduleReminder(context)
        }
    }

}
