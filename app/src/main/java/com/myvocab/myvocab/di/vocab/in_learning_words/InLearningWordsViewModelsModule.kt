package com.myvocab.myvocab.di.vocab.in_learning_words

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Binds
import dagger.multibindings.IntoMap
import com.myvocab.myvocab.di.ViewModelKey
import com.myvocab.myvocab.ui.vocab.in_learning_words.InLearningWordsViewModel

@Module
abstract class InLearningWordsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(InLearningWordsViewModel::class)
    abstract fun bindInLearningWordsViewModel(viewModel: InLearningWordsViewModel): ViewModel

}
