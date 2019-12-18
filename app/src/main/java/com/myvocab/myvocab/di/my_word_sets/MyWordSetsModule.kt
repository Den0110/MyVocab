package com.myvocab.myvocab.di.my_word_sets

import com.myvocab.myvocab.data.model.WordSetDiffCallback
import dagger.Module
import dagger.Provides

@Module
class MyWordSetsModule {

    @Provides
    fun provideWordSetDiffCallback(): WordSetDiffCallback = WordSetDiffCallback()

}
