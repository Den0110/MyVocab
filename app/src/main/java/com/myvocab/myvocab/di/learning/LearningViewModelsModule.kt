package com.myvocab.myvocab.di.learning

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.ui.learning.LearningViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LearningViewModelsModule {

    @Binds
    @IntoMap
    @com.myvocab.myvocab.di.ViewModelKey(LearningViewModel::class)
    abstract fun bindLearningViewModel(viewModel: LearningViewModel): ViewModel

}