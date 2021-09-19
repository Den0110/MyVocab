package com.myvocab.myvocab.di

import android.app.Application
import com.myvocab.myvocab.MyVocabApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ActivityBuilderModule::class, AppModule::class, ViewModelFactoryModule::class])
interface AppComponent : AndroidInjector<MyVocabApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
