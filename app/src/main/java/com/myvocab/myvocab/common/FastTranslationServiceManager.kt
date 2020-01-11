package com.myvocab.myvocab.common

import android.app.ActivityManager
import android.content.Context
import com.myvocab.myvocab.common.fasttranslation.FastTranslationService
import com.myvocab.myvocab.util.PreferencesManager
import timber.log.Timber
import javax.inject.Inject

class FastTranslationServiceManager
@Inject
constructor(
        private val context: Context,
        private val prefManager: PreferencesManager
){

    companion object {
        private const val TAG = "FastTranslationServiceManager"
    }

    fun start() {
        prefManager.setFastTranslationState(true)
        if(!isServiceRunning())
            FastTranslationService.start(context)
    }

    fun startIfEnabled() {
        val state = prefManager.getFastTranslationState()
        if(state) {
            Timber.v("FastTranslationServiceStarter: checking service state...")
            if (!isServiceRunning()) {
                Timber.d("Restarting service")
                start()
            } else {
                Timber.v("Service running")
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
        prefManager.setFastTranslationState(false)
        if(isServiceRunning())
            FastTranslationService.stop(context)
    }

}