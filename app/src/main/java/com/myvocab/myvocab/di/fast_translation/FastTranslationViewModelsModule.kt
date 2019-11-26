package com.myvocab.myvocab.di.fast_translation

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.ui.fast_translation.FastTranslationWidgetViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FastTranslationViewModelsModule {

    @Binds
    @IntoMap
    @com.myvocab.myvocab.di.ViewModelKey(FastTranslationWidgetViewModel::class)
    abstract fun bindTranslationViewModel(viewModel: FastTranslationWidgetViewModel): ViewModel

}
