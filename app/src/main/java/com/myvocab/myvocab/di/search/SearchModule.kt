package com.myvocab.myvocab.di.search

import com.myvocab.myvocab.data.model.LoadWordSetUseCaseResultDiffCallback
import dagger.Module
import dagger.Provides

@Module
class SearchModule {

    @Provides
    fun provideLoadWordSetUseCaseResultDiffCallback(): LoadWordSetUseCaseResultDiffCallback =
            LoadWordSetUseCaseResultDiffCallback()

}