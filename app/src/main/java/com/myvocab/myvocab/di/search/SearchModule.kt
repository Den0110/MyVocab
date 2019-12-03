package com.myvocab.myvocab.di.search

import com.myvocab.myvocab.data.model.WordSetDiffCallback
import dagger.Module
import dagger.Provides

@Module
class SearchModule {

    @Provides
    fun provideWordSetDiffCallback() : WordSetDiffCallback = WordSetDiffCallback()

}