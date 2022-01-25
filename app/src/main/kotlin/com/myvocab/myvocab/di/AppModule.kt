package com.myvocab.myvocab.di

import android.app.Application
import android.content.Context
import com.myvocab.core.system.ResourceManager
import com.myvocab.core.util.PreferencesManager
import com.myvocab.domain.service.FastTranslationServiceManager
import com.myvocab.domain.service.ReminderScheduler
import com.myvocab.fasttranslation.FastTranslationServiceManagerImpl
import com.myvocab.fasttranslation.ReminderSchedulerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun context(app: Application): Context = app

    @Provides
    @Singleton
    fun providePreferencesManager(app: Application) = PreferencesManager(app)

    @Provides
    @Singleton
    fun provideReminderScheduler(
        app: Application,
        prefManager: PreferencesManager
    ): ReminderScheduler = ReminderSchedulerImpl(app, prefManager)

    @Provides
    @Singleton
    fun provideFastTranslationServiceManager(
        app: Application,
        prefManager: PreferencesManager
    ): FastTranslationServiceManager = FastTranslationServiceManagerImpl(app, prefManager)

    @Provides
    @Singleton
    fun provideResourceManager(app: Application): ResourceManager = ResourceManager(app)

}
