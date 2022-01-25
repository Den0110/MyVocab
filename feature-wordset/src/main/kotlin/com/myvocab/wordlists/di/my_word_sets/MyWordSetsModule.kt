package com.myvocab.wordlists.di.my_word_sets

import com.myvocab.domain.usecases.wordset.LoadWordSetUseCaseResultDiffCallback
import dagger.Module
import dagger.Provides

@Module
class MyWordSetsModule {

    @Provides
    fun provideLoadWordSetUseCaseResultDiffCallback(): LoadWordSetUseCaseResultDiffCallback =
            LoadWordSetUseCaseResultDiffCallback()

}
