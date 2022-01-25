package com.myvocab.learning.di

import androidx.lifecycle.ViewModel
import com.myvocab.core.di.ViewModelKey
import com.myvocab.learning.LearningViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LearningViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(LearningViewModel::class)
    abstract fun bindLearningViewModel(viewModel: LearningViewModel): ViewModel

}