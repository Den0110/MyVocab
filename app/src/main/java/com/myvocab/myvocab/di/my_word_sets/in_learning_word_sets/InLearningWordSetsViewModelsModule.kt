package com.myvocab.myvocab.di.my_word_sets.in_learning_word_sets

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Binds
import dagger.multibindings.IntoMap
import com.myvocab.myvocab.di.ViewModelKey
import com.myvocab.myvocab.ui.my_word_sets.in_learning_words.InLearningWordSetsViewModel

@Module
abstract class InLearningWordSetsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(InLearningWordSetsViewModel::class)
    abstract fun bindInLearningWordsViewModel(viewModel: InLearningWordSetsViewModel): ViewModel

}
