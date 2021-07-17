package com.myvocab.myvocab.di.my_word_sets.in_learning_word_sets

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.di.ViewModelKey
import com.myvocab.myvocab.ui.word_sets.in_learning_words.InLearningWordSetsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class InLearningWordSetsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(InLearningWordSetsViewModel::class)
    abstract fun bindInLearningWordsViewModel(viewModel: InLearningWordSetsViewModel): ViewModel

}
