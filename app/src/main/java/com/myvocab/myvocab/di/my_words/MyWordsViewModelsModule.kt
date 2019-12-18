package com.myvocab.myvocab.di.my_words

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.ui.my_words.MyWordsViewModel
import com.myvocab.myvocab.ui.word_set_details.WordSetDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MyWordsViewModelsModule {

    @Binds
    @IntoMap
    @com.myvocab.myvocab.di.ViewModelKey(MyWordsViewModel::class)
    abstract fun bindMyWordsViewModel(viewModel: MyWordsViewModel): ViewModel

}