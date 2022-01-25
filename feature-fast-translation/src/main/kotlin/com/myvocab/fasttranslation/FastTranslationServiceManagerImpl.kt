package com.myvocab.fasttranslation

import android.app.ActivityManager
import android.content.Context
import com.myvocab.core.util.PreferencesManager
import com.myvocab.domain.service.FastTranslationServiceManager
import com.myvocab.fasttranslation.fasttranslation.FastTranslationService
import timber.log.Timber
import javax.inject.Inject

class FastTranslationServiceManagerImpl
@Inject
constructor(
        private val context: Context,
        private val prefManager: PreferencesManager
) : FastTranslationServiceManager {

    override fun start() {
        prefManager.fastTranslationState = true
        if (!isServiceRunning())
            FastTranslationService.start(context)
    }

    override fun startIfEnabled() {
        val state = prefManager.fastTranslationState
        if (state) {
            Timber.v("FastTranslationServiceStarter: checking service state...")
            if (!isServiceRunning()) {
                Timber.d("Restarting service")
                start()
            } else {
                Timber.v("Service running")
            }
        }
    }

    private fun isServiceRunning(): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FastTranslationService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun isTranslationEnabled() = prefManager.fastTranslationState

    override fun cancel() {
        prefManager.fastTranslationState = false
        if (isServiceRunning())
            FastTranslationService.stop(context)
    }

}