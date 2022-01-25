package com.myvocab.fasttranslation.di

import androidx.lifecycle.ViewModel
import com.myvocab.core.di.ViewModelKey
import com.myvocab.fasttranslation.FastTranslationWidgetViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FastTranslationViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(FastTranslationWidgetViewModel::class)
    abstract fun bindTranslationViewModel(viewModel: FastTranslationWidgetViewModel): ViewModel

}
