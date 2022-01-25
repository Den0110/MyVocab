package com.myvocab.wordlists.di.my_word_sets.learned_word_sets

import androidx.lifecycle.ViewModel
import com.myvocab.core.di.ViewModelKey
import com.myvocab.wordlists.wordsets.learned_words.LearnedWordSetsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LearnedWordSetsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(LearnedWordSetsViewModel::class)
    abstract fun bindLearnedWordsViewModel(viewModel: LearnedWordSetsViewModel): ViewModel

}
