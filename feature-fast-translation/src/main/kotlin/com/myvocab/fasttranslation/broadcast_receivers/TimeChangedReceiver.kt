package com.myvocab.fasttranslation.broadcast_receivers

import android.content.Context
import android.content.Intent
import com.myvocab.fasttranslation.FastTranslationServiceManagerImpl
import com.myvocab.fasttranslation.ReminderSchedulerImpl
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

class TimeChangedReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var reminderScheduler: ReminderSchedulerImpl

    @Inject
    lateinit var translationServiceManager: FastTranslationServiceManagerImpl

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if(intent.action == Intent.ACTION_TIME_CHANGED || intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
            Timber.d("Time changed")
            translationServiceManager.startIfEnabled()
            reminderScheduler.scheduleIfEnabled()
        }
    }

}