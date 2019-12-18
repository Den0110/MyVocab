package com.myvocab.myvocab.di.my_word_sets.learned_word_sets

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Binds
import dagger.multibindings.IntoMap
import com.myvocab.myvocab.di.ViewModelKey
import com.myvocab.myvocab.ui.my_word_sets.learned_words.LearnedWordSetsViewModel

@Module
abstract class LearnedWordSetsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(LearnedWordSetsViewModel::class)
    abstract fun bindLearnedWordsViewModel(viewModel: LearnedWordSetsViewModel): ViewModel

}
