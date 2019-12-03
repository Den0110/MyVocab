package com.myvocab.myvocab.di

import com.myvocab.myvocab.common.fasttranslation.FastTranslationService
import com.myvocab.myvocab.di.fast_translation.FastTranslationModule
import com.myvocab.myvocab.di.fast_translation.FastTranslationViewModelsModule
import com.myvocab.myvocab.ui.vocab.VocabFragment
import com.myvocab.myvocab.di.vocab.VocabModule
import com.myvocab.myvocab.di.vocab.in_learning_words.InLearningWordsViewModelsModule
import com.myvocab.myvocab.ui.add_new_word.AddNewWordActivity
import com.myvocab.myvocab.di.add_new_word.AddNewWordViewModelsModule
import com.myvocab.myvocab.di.vocab.learned_words.LearnedWordsViewModelsModule
import com.myvocab.myvocab.ui.vocab.learned_words.LearnedWordsFragment
import com.myvocab.myvocab.ui.learning.LearningFragment
import com.myvocab.myvocab.di.learning.LearningViewModelsModule
import com.myvocab.myvocab.di.search.SearchModule
import com.myvocab.myvocab.di.search.SearchViewModelsModule
import com.myvocab.myvocab.ui.settings.SettingsFragment
import com.myvocab.myvocab.di.settings.SettingsViewModelsModule
import com.myvocab.myvocab.di.vocab.in_learning_words.InLearningWordsFragment
import com.myvocab.myvocab.ui.search.SearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FastTranslationModule::class, FastTranslationViewModelsModule::class])
    abstract fun contributeTranslationService(): FastTranslationService

    @ContributesAndroidInjector(modules = [LearningViewModelsModule::class])
    abstract fun contributeLearningFragment(): LearningFragment

    @ContributesAndroidInjector(modules = [VocabModule::class])
    abstract fun contributeVocabFragment(): VocabFragment

    @ContributesAndroidInjector(modules = [VocabModule::class, InLearningWordsViewModelsModule::class])
    abstract fun contributeInLearningWordsFragment(): InLearningWordsFragment

    @ContributesAndroidInjector(modules = [VocabModule::class, LearnedWordsViewModelsModule::class])
    abstract fun contributeLearnedWordsFragment(): LearnedWordsFragment

    @ContributesAndroidInjector(modules = [AddNewWordViewModelsModule::class])
    abstract fun contributeAddNewWordActivity(): AddNewWordActivity

    @ContributesAndroidInjector(modules = [SearchModule::class, SearchViewModelsModule::class])
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector(modules = [SettingsViewModelsModule::class])
    abstract fun contributeSettingsFragment(): SettingsFragment

}
