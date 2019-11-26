package com.myvocab.myvocab.di.vocab

import com.myvocab.myvocab.data.model.WordDiffCallback
import dagger.Module
import dagger.Provides

@Module
class VocabModule {

    @Provides
    fun provideWordDiffCallback(): WordDiffCallback = WordDiffCallback()

}
