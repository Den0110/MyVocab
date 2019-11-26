package com.myvocab.myvocab.common.fasttranslation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.myvocab.myvocab.util.getFastTranslationState
import com.myvocab.myvocab.util.isServiceRunning

class FastTranslationServiceStarter : BroadcastReceiver() {

    companion object {
        const val TAG = "FastTranslationService"

        fun start(context: Context){
            context.sendBroadcast(Intent(context, FastTranslationServiceStarter::class.java))
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val state = getFastTranslationState(context)
        if(state) {
            Log.v(TAG, "FastTranslationServiceStarter: checking service state...")
            if (!isServiceRunning(context, FastTranslationService::class.java)) {
                FastTranslationService.start(context)
                Log.d(TAG, "Restart service")
            } else {
                Log.v(TAG, "Service running")
            }
        }
    }

}