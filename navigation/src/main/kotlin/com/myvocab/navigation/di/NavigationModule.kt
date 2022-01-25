package com.myvocab.navigation.di

import com.myvocab.navigation.Navigator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NavigationModule {

    @Singleton
    @Provides
    fun navigator() = Navigator()

}