package com.myvocab.myvocab

import android.content.Context
import androidx.multidex.MultiDex
import com.google.android.gms.ads.MobileAds
import com.myvocab.myvocab.common.FastTranslationServiceManager
import com.myvocab.myvocab.common.ReminderScheduler
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.data.source.local.Database
import com.myvocab.myvocab.di.DaggerAppComponent
import com.myvocab.myvocab.util.createFastTranslationNotificationChannel
import com.myvocab.myvocab.util.createReminderNotificationChannel
import com.myvocab.myvocab.util.createTranslationNotificationChannel
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

class MyVocabApp : DaggerApplication() {

    @Inject lateinit var wordsDb: Database
    @Inject lateinit var wordRepository: WordRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var translationServiceManager: FastTranslationServiceManager

    var started: Boolean = false

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        createFastTranslationNotificationChannel(this)
        createReminderNotificationChannel(this)
        createTranslationNotificationChannel(this)

        translationServiceManager.startIfEnabled()
        reminderScheduler.scheduleIfEnabled()

        // just to create and pre-populate database
        wordsDb.wordSetsDao().getWordSets().subscribe({},{})

        RxJavaPlugins.setErrorHandler {
            Timber.e(it)
        }

        MobileAds.initialize(this)

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder().application(this).build()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}
