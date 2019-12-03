package com.myvocab.myvocab.di.search

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.ui.search.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SearchViewModelsModule {

    @Binds
    @IntoMap
    @com.myvocab.myvocab.di.ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

}