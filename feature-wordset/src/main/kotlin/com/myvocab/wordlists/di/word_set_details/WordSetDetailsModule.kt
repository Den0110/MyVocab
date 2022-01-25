package com.myvocab.wordlists.di.word_set_details

import com.myvocab.domain.entities.WordDiffCallback
import dagger.Module
import dagger.Provides

@Module
class WordSetDetailsModule {

    @Provides
    fun provideWordDiffCallback(): WordDiffCallback = WordDiffCallback()

}