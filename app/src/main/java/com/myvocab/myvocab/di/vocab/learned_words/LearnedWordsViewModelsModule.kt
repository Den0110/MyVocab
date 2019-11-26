package com.myvocab.myvocab.di.vocab.learned_words

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Binds
import dagger.multibindings.IntoMap
import com.myvocab.myvocab.di.ViewModelKey
import com.myvocab.myvocab.ui.vocab.learned_words.LearnedWordsViewModel

@Module
abstract class LearnedWordsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(LearnedWordsViewModel::class)
    abstract fun bindLearnedWordsViewModel(viewModel: LearnedWordsViewModel): ViewModel

}
