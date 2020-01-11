package com.myvocab.myvocab.common.fasttranslation

import android.content.Context
import android.content.Intent
import com.myvocab.myvocab.common.FastTranslationServiceManager
import dagger.android.DaggerBroadcastReceiver
import javax.inject.Inject

class FastTranslationServiceStarter : DaggerBroadcastReceiver() {

    @Inject
    lateinit var translationServiceManager: FastTranslationServiceManager

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        translationServiceManager.startIfEnabled()
    }

}