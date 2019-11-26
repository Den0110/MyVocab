package com.myvocab.myvocab.di.fast_translation

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.myvocab.myvocab.data.source.remote.translation.TranslationApi

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class FastTranslationModule {

    @Provides
    fun provideTranslateApi(retrofit: Retrofit): TranslationApi = retrofit.create(TranslationApi::class.java)

    @Provides
    fun provideWindowManager(application: Application): WindowManager =
            application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    @Provides
    fun provideLayoutInflater(application: Application): LayoutInflater =
            application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

}
