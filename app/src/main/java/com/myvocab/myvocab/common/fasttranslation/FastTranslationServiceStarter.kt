package com.myvocab.myvocab.common.fasttranslation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.myvocab.myvocab.util.getFastTranslationState
import com.myvocab.myvocab.util.isServiceRunning
import timber.log.Timber

class FastTranslationServiceStarter : BroadcastReceiver() {

    companion object {
        private const val TAG = "FastTranslationService"

        fun start(context: Context){
            context.sendBroadcast(Intent(context, FastTranslationServiceStarter::class.java))
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val state = getFastTranslationState(context)
        if(state) {
            Timber.v(TAG, "FastTranslationServiceStarter: checking service state...")
            if (!isServiceRunning(context, FastTranslationService::class.java)) {
                FastTranslationService.start(context)
                Timber.d(TAG, "Restart service")
            } else {
                Timber.v(TAG, "Service running")
            }
        }
    }

}