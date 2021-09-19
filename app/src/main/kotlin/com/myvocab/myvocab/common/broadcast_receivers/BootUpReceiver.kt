package com.myvocab.myvocab.common.broadcast_receivers

import android.content.Context
import android.content.Intent
import com.myvocab.myvocab.common.FastTranslationServiceManager
import com.myvocab.myvocab.common.ReminderScheduler
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

class BootUpReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var translationServiceManager: FastTranslationServiceManager

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if(intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Timber.d("Boot up")
            translationServiceManager.startIfEnabled()
            reminderScheduler.scheduleIfEnabled()
        }
    }

}
