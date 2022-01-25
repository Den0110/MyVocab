package com.myvocab.fasttranslation.broadcast_receivers

import android.content.Context
import android.content.Intent
import com.myvocab.fasttranslation.FastTranslationServiceManagerImpl
import com.myvocab.fasttranslation.ReminderSchedulerImpl
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

class BootUpReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderSchedulerImpl

    @Inject
    lateinit var translationServiceManager: FastTranslationServiceManagerImpl

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if(intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Timber.d("Boot up")
            translationServiceManager.startIfEnabled()
            reminderScheduler.scheduleIfEnabled()
        }
    }

}
