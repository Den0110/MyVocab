package com.myvocab.wordlists.di.my_words

import androidx.lifecycle.ViewModel
import com.myvocab.core.di.ViewModelKey
import com.myvocab.wordlists.my_words.MyWordsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MyWordsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MyWordsViewModel::class)
    abstract fun bindMyWordsViewModel(viewModel: MyWordsViewModel): ViewModel

}