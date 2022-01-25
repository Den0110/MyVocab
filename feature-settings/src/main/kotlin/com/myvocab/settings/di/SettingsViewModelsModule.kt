package com.myvocab.settings.di

import androidx.lifecycle.ViewModel
import com.myvocab.core.di.ViewModelKey
import com.myvocab.settings.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SettingsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

}