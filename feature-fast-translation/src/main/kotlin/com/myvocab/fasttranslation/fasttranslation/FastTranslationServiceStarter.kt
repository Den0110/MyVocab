package com.myvocab.fasttranslation.fasttranslation

import android.content.Context
import android.content.Intent
import com.myvocab.fasttranslation.FastTranslationServiceManagerImpl
import dagger.android.DaggerBroadcastReceiver
import javax.inject.Inject

class FastTranslationServiceStarter : DaggerBroadcastReceiver() {

    @Inject
    lateinit var translationServiceManager: FastTranslationServiceManagerImpl

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        translationServiceManager.startIfEnabled()
    }

}