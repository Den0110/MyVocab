package com.myvocab.wordlists.di.search

import androidx.lifecycle.ViewModel
import com.myvocab.core.di.ViewModelKey
import com.myvocab.wordlists.wordsets.all.AllWordSetsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SearchViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(AllWordSetsViewModel::class)
    abstract fun bindSearchViewModel(viewModel: AllWordSetsViewModel): ViewModel

}