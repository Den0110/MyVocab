package com.myvocab.myvocab

import android.content.Context
import androidx.multidex.MultiDex
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import com.myvocab.myvocab.di.DaggerAppComponent
import com.myvocab.myvocab.util.createFastTranslationNotificationChannel
import com.myvocab.myvocab.util.createReminderNotificationChannel
import com.squareup.leakcanary.LeakCanary
import com.myvocab.myvocab.util.scheduleReminder


class MyVocabApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)

        if (BuildConfig.USE_LEAK_CANARY) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return
            }
            LeakCanary.install(this)
        }

        createFastTranslationNotificationChannel(this)
        createReminderNotificationChannel(this)

        scheduleReminder(this)

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerAppComponent.builder().application(this).build()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}
