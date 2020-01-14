package com.myvocab.myvocab.di.my_word_sets

import com.myvocab.myvocab.data.model.LoadWordSetUseCaseResultDiffCallback
import dagger.Module
import dagger.Provides

@Module
class MyWordSetsModule {

    @Provides
    fun provideLoadWordSetUseCaseResultDiffCallback(): LoadWordSetUseCaseResultDiffCallback =
            LoadWordSetUseCaseResultDiffCallback()

}
