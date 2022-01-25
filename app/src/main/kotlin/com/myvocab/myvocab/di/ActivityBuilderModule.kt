package com.myvocab.myvocab.di

import com.myvocab.fasttranslation.broadcast_receivers.BootUpReceiver
import com.myvocab.fasttranslation.broadcast_receivers.ReminderReceiver
import com.myvocab.fasttranslation.broadcast_receivers.TimeChangedReceiver
import com.myvocab.fasttranslation.di.FastTranslationModule
import com.myvocab.fasttranslation.di.FastTranslationViewModelsModule
import com.myvocab.fasttranslation.fasttranslation.FastTranslationService
import com.myvocab.fasttranslation.fasttranslation.FastTranslationServiceStarter
import com.myvocab.learning.LearningFragment
import com.myvocab.learning.di.LearningViewModelsModule
import com.myvocab.myvocab.ui.IntroActivity
import com.myvocab.myvocab.ui.MainActivity
import com.myvocab.myvocab.ui.intro.IntroEnableFastTranslationFragment
import com.myvocab.settings.SettingsFragment
import com.myvocab.settings.di.SettingsViewModelsModule
import com.myvocab.wordlists.add_new_word.AddNewWordFragment
import com.myvocab.wordlists.di.add_new_word.AddNewWordViewModelsModule
import com.myvocab.wordlists.di.my_word_sets.MyWordSetsModule
import com.myvocab.wordlists.di.my_word_sets.in_learning_word_sets.InLearningWordSetsViewModelsModule
import com.myvocab.wordlists.di.my_word_sets.learned_word_sets.LearnedWordSetsViewModelsModule
import com.myvocab.wordlists.di.my_words.MyWordsViewModelsModule
import com.myvocab.wordlists.di.search.SearchModule
import com.myvocab.wordlists.di.search.SearchViewModelsModule
import com.myvocab.wordlists.di.word_set_details.WordSetDetailsModule
import com.myvocab.wordlists.di.word_set_details.WordSetDetailsViewModelsModule
import com.myvocab.wordlists.my_words.MyWordsFragment
import com.myvocab.wordlists.wordset_details.WordSetDetailsFragment
import com.myvocab.wordlists.wordsets.WordSetsFragment
import com.myvocab.wordlists.wordsets.all.AllWordSetsFragment
import com.myvocab.wordlists.wordsets.in_learning_words.InLearningWordSetsFragment
import com.myvocab.wordlists.wordsets.learned_words.LearnedWordSetsFragment
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

    @ContributesAndroidInjector(modules = [WordSetDetailsModule::class, MyWordsViewModelsModule::class])
    abstract fun contributeMyWordsFragment(): MyWordsFragment

    @ContributesAndroidInjector(modules = [MyWordSetsModule::class])
    abstract fun contributeMyWordSetsFragment(): WordSetsFragment

    @ContributesAndroidInjector(modules = [MyWordSetsModule::class, InLearningWordSetsViewModelsModule::class])
    abstract fun contributeInLearningWordSetsFragment(): InLearningWordSetsFragment

    @ContributesAndroidInjector(modules = [MyWordSetsModule::class, LearnedWordSetsViewModelsModule::class])
    abstract fun contributeLearnedWordSetsFragment(): LearnedWordSetsFragment

    @ContributesAndroidInjector(modules = [AddNewWordViewModelsModule::class])
    abstract fun contributeAddNewWordFragment(): AddNewWordFragment

    @ContributesAndroidInjector(modules = [WordSetDetailsModule::class, WordSetDetailsViewModelsModule::class])
    abstract fun contributeWordSetDetailsFragment(): WordSetDetailsFragment

    @ContributesAndroidInjector(modules = [SearchModule::class, SearchViewModelsModule::class])
    abstract fun contributeSearchFragment(): AllWordSetsFragment

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
