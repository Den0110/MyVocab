package com.myvocab.wordlists.di.search

import com.myvocab.domain.usecases.wordset.LoadWordSetUseCaseResultDiffCallback
import dagger.Module
import dagger.Provides

@Module
class SearchModule {

    @Provides
    fun provideLoadWordSetUseCaseResultDiffCallback(): LoadWordSetUseCaseResultDiffCallback =
            LoadWordSetUseCaseResultDiffCallback()

}