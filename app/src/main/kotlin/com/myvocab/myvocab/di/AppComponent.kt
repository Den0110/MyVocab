package com.myvocab.myvocab.di

import android.app.Application
import com.myvocab.data.di.DataModule
import com.myvocab.domain.di.DomainModule
import com.myvocab.myvocab.MyVocabApp
import com.myvocab.navigation.di.NavigationModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuilderModule::class,
        AppModule::class,
        ViewModelFactoryModule::class,
        NavigationModule::class,
        DomainModule::class,
        DataModule::class,
    ]
)
interface AppComponent : AndroidInjector<MyVocabApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
