package com.myvocab.myvocab.di.add_new_word

import dagger.Module
import dagger.Binds
import dagger.multibindings.IntoMap
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.ui.add_new_word.AddNewWordViewModel

@Module
abstract class AddNewWordViewModelsModule {

    @Binds
    @IntoMap
    @com.myvocab.myvocab.di.ViewModelKey(AddNewWordViewModel::class)
    abstract fun bindAddNewWordViewModel(viewModel: AddNewWordViewModel): ViewModel

}