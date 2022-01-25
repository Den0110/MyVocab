package com.myvocab.wordlists.di.add_new_word

import androidx.lifecycle.ViewModel
import com.myvocab.core.di.ViewModelKey
import com.myvocab.wordlists.add_new_word.AddNewWordViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AddNewWordViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddNewWordViewModel::class)
    abstract fun bindAddNewWordViewModel(viewModel: AddNewWordViewModel): ViewModel

}