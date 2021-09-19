package com.myvocab.myvocab.di.add_new_word

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.ui.add_new_word.AddNewWordViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AddNewWordViewModelsModule {

    @Binds
    @IntoMap
    @com.myvocab.myvocab.di.ViewModelKey(AddNewWordViewModel::class)
    abstract fun bindAddNewWordViewModel(viewModel: AddNewWordViewModel): ViewModel

}