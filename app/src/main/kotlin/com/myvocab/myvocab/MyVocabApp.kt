package com.myvocab.myvocab

import android.content.Context
import androidx.multidex.MultiDex
import com.google.android.gms.ads.MobileAds
import com.myvocab.core.util.createFastTranslationNotificationChannel
import com.myvocab.core.util.createReminderNotificationChannel
import com.myvocab.core.util.createTranslationNotificationChannel
import com.myvocab.data.source.local.Database
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.fasttranslation.FastTranslationServiceManagerImpl
import com.myvocab.fasttranslation.ReminderSchedulerImpl
import com.myvocab.myvocab.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

class MyVocabApp : DaggerApplication() {

    @Inject lateinit var wordsDb: Database
    @Inject lateinit var wordRepository: WordRepository
    @Inject lateinit var reminderScheduler: ReminderSchedulerImpl
    @Inject lateinit var translationServiceManager: FastTranslationServiceManagerImpl

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
        GlobalScope.launch { wordsDb.wordSetsDao().getWordSets() }

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
