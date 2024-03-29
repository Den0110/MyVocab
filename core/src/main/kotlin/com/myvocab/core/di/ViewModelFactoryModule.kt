package com.myvocab.core.di

import androidx.lifecycle.ViewModelProvider
import com.myvocab.core.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory

}
