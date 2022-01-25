package com.myvocab.wordlists.di.word_set_details

import androidx.lifecycle.ViewModel
import com.myvocab.core.di.ViewModelKey
import com.myvocab.wordlists.wordset_details.WordSetDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WordSetDetailsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(WordSetDetailsViewModel::class)
    abstract fun bindWordSetDetailsViewModel(viewModel: WordSetDetailsViewModel): ViewModel

}