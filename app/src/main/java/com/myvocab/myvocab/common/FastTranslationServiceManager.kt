package com.myvocab.myvocab.common

import android.app.ActivityManager
import android.content.Context
import com.myvocab.myvocab.common.fasttranslation.FastTranslationService
import com.myvocab.myvocab.util.PreferencesManager
import com.myvocab.myvocab.util.canStartFastTranslation
import timber.log.Timber
import javax.inject.Inject

class FastTranslationServiceManager
@Inject
constructor(
        private val context: Context,
        private val prefManager: PreferencesManager
){

    fun start() {
        prefManager.fastTranslationState = true
        if(!isServiceRunning())
            FastTranslationService.start(context)
    }

    fun startIfEnabled() {
        val state = prefManager.fastTranslationState
        if(state) {
            if(canStartFastTranslation(context)) {
                Timber.v("FastTranslationServiceStarter: checking service state...")
                if (!isServiceRunning()) {
                    Timber.d("Restarting service")
                    start()
                } else {
                    Timber.v("Service running")
                }
            } else {
                cancel()
            }
        }
    }

    fun isServiceRunning(): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FastTranslationService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun cancel() {
        prefManager.fastTranslationState = false
        if(isServiceRunning())
            FastTranslationService.stop(context)
    }

}