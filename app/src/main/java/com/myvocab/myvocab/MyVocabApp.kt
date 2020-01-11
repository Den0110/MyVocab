package com.myvocab.myvocab

import android.content.Context
import androidx.multidex.MultiDex
import com.myvocab.myvocab.common.FastTranslationServiceManager
import com.myvocab.myvocab.common.ReminderScheduler
import com.myvocab.myvocab.data.source.local.Database
import com.myvocab.myvocab.di.DaggerAppComponent
import com.myvocab.myvocab.util.createFastTranslationNotificationChannel
import com.myvocab.myvocab.util.createReminderNotificationChannel
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


class MyVocabApp : DaggerApplication() {

    @Inject lateinit var wordsDb: Database
    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var translationServiceManager: FastTranslationServiceManager

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)

        if (BuildConfig.USE_LEAK_CANARY) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return
            }
            LeakCanary.install(this)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        createFastTranslationNotificationChannel(this)
        createReminderNotificationChannel(this)

        translationServiceManager.startIfEnabled()
        reminderScheduler.scheduleIfEnabled()

        // just to create and pre-populate database
        wordsDb.wordSetsDao().getWordSets().subscribe({},{})

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder().application(this).build()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}
