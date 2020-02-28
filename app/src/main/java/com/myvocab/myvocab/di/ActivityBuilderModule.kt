package com.myvocab.myvocab.di

import com.myvocab.myvocab.common.broadcast_receivers.BootUpReceiver
import com.myvocab.myvocab.common.broadcast_receivers.ReminderReceiver
import com.myvocab.myvocab.common.broadcast_receivers.TimeChangedReceiver
import com.myvocab.myvocab.common.fasttranslation.FastTranslationService
import com.myvocab.myvocab.common.fasttranslation.FastTranslationServiceStarter
import com.myvocab.myvocab.di.fast_translation.FastTranslationModule
import com.myvocab.myvocab.di.fast_translation.FastTranslationViewModelsModule
import com.myvocab.myvocab.di.my_word_sets.MyWordSetsModule
import com.myvocab.myvocab.di.my_word_sets.in_learning_word_sets.InLearningWordSetsViewModelsModule
import com.myvocab.myvocab.ui.add_new_word.AddNewWordFragment
import com.myvocab.myvocab.di.add_new_word.AddNewWordViewModelsModule
import com.myvocab.myvocab.di.my_word_sets.learned_word_sets.LearnedWordSetsViewModelsModule
import com.myvocab.myvocab.ui.my_word_sets.learned_words.LearnedWordSetsFragment
import com.myvocab.myvocab.ui.learning.LearningFragment
import com.myvocab.myvocab.di.learning.LearningViewModelsModule
import com.myvocab.myvocab.di.my_words.MyWordsViewModelsModule
import com.myvocab.myvocab.di.search.SearchModule
import com.myvocab.myvocab.di.search.SearchViewModelsModule
import com.myvocab.myvocab.ui.settings.SettingsFragment
import com.myvocab.myvocab.di.settings.SettingsViewModelsModule
import com.myvocab.myvocab.di.word_set_details.WordSetDetailsModule
import com.myvocab.myvocab.di.word_set_details.WordSetDetailsViewModelsModule
import com.myvocab.myvocab.ui.MainActivity
import com.myvocab.myvocab.ui.IntroActivity
import com.myvocab.myvocab.ui.intro.IntroEnableFastTranslationFragment
import com.myvocab.myvocab.ui.my_word_sets.MyWordSetsFragment
import com.myvocab.myvocab.ui.my_word_sets.in_learning_words.InLearningWordSetsFragment
import com.myvocab.myvocab.ui.my_words.MyWordsFragment
import com.myvocab.myvocab.ui.search.SearchFragment
import com.myvocab.myvocab.ui.vocab.VocabFragment
import com.myvocab.myvocab.ui.word_set_details.WordSetDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    /**
     *  Activities
     */

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeIntroActivity(): IntroActivity

    /**
     *  Fragments
     */

    @ContributesAndroidInjector(modules = [FastTranslationModule::class, FastTranslationViewModelsModule::class])
    abstract fun contributeTranslationService(): FastTranslationService

    @ContributesAndroidInjector(modules = [LearningViewModelsModule::class])
    abstract fun contributeLearningFragment(): LearningFragment

    @ContributesAndroidInjector
    abstract fun contributeVocabFragment(): VocabFragment

    @ContributesAndroidInjector(modules = [WordSetDetailsModule::class, MyWordsViewModelsModule::class])
    abstract fun contributeMyWordsFragment(): MyWordsFragment

    @ContributesAndroidInjector(modules = [MyWordSetsModule::class])
    abstract fun contributeMyWordSetsFragment(): MyWordSetsFragment

    @ContributesAndroidInjector(modules = [MyWordSetsModule::class, InLearningWordSetsViewModelsModule::class])
    abstract fun contributeInLearningWordSetsFragment(): InLearningWordSetsFragment

    @ContributesAndroidInjector(modules = [MyWordSetsModule::class, LearnedWordSetsViewModelsModule::class])
    abstract fun contributeLearnedWordSetsFragment(): LearnedWordSetsFragment

    @ContributesAndroidInjector(modules = [AddNewWordViewModelsModule::class])
    abstract fun contributeAddNewWordFragment(): AddNewWordFragment

    @ContributesAndroidInjector(modules = [WordSetDetailsModule::class, WordSetDetailsViewModelsModule::class])
    abstract fun contributeWordSetDetailsFragment(): WordSetDetailsFragment

    @ContributesAndroidInjector(modules = [SearchModule::class, SearchViewModelsModule::class])
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector(modules = [SettingsViewModelsModule::class])
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector()
    abstract fun contributeIntroEnableFastTranslationFragment(): IntroEnableFastTranslationFragment


    /**
     *  Broadcast receivers
     */

    @ContributesAndroidInjector
    abstract fun contributeReminderReceiver(): ReminderReceiver

    @ContributesAndroidInjector
    abstract fun contributeBootUpReceiver(): BootUpReceiver

    @ContributesAndroidInjector
    abstract fun contributeTimeChanchedReceiver(): TimeChangedReceiver

    @ContributesAndroidInjector
    abstract fun contributeFastTranslationServiceStarter(): FastTranslationServiceStarter

}
