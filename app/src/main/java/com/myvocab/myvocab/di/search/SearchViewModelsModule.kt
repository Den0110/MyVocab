package com.myvocab.myvocab.di.search

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.ui.word_sets.all.AllWordSetsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SearchViewModelsModule {

    @Binds
    @IntoMap
    @com.myvocab.myvocab.di.ViewModelKey(AllWordSetsViewModel::class)
    abstract fun bindSearchViewModel(viewModel: AllWordSetsViewModel): ViewModel

}