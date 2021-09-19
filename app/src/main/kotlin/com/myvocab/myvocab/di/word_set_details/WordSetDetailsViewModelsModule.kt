package com.myvocab.myvocab.di.word_set_details

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.ui.word_set_details.WordSetDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WordSetDetailsViewModelsModule {

    @Binds
    @IntoMap
    @com.myvocab.myvocab.di.ViewModelKey(WordSetDetailsViewModel::class)
    abstract fun bindWordSetDetailsViewModel(viewModel: WordSetDetailsViewModel): ViewModel

}