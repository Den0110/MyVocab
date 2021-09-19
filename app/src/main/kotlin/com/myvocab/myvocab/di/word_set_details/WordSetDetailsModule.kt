package com.myvocab.myvocab.di.word_set_details

import com.myvocab.myvocab.data.model.WordDiffCallback
import dagger.Module
import dagger.Provides

@Module
class WordSetDetailsModule {

    @Provides
    fun provideWordDiffCallback(): WordDiffCallback = WordDiffCallback()

}