package com.myvocab.myvocab.util

import android.content.Context
import com.myvocab.myvocab.common.ReminderScheduler

class PreferencesManager(context: Context) {

    companion object {
        const val APP_PREFERENCES = "APP_PREFERENCES"
    }

    private val preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    /**
     *  First Start of App
     */

    var introShowed: Boolean
        set(value) = preferences
                .edit()
                .putBoolean("intro_showed", value)
                .apply()
        get() = preferences.getBoolean("intro_showed", false)

    var fastTranslationGuideShowed: Boolean
        set(value) = preferences
                .edit()
                .putBoolean("fast_translation_guide_showed", value)
                .apply()
        get() = preferences.getBoolean("fast_translation_guide_showed", false)

    /**
     *  Fast Translation
     */

    var fastTranslationState: Boolean
        set(value) = preferences
                .edit()
                .putBoolean("fast_translation_state", value)
                .apply()
        get() = preferences.getBoolean("fast_translation_state", false)

    /**
     *  Reminder
     */

    var remindingState: Boolean
        set(value) = preferences
                .edit()
                .putBoolean("reminding_state", value)
                .apply()
        get() = preferences.getBoolean("reminding_state", true)

    var remindOnlyWordsToLearn: Boolean
        set(value) =
            preferences
                    .edit()
                    .putBoolean("remind_only_words_to_learn", value)
                    .apply()
        get() = preferences.getBoolean("remind_only_words_to_learn", false)

    var remindingTime: Long
        set(value) =
            preferences
                    .edit()
                    .putLong("reminding_time", value)
                    .apply()
        get() = preferences.getLong("reminding_time", ReminderScheduler.REMINDER_DEFAULT_TIME)

    /**
     *  Learning
     */

    // last word that was chosen (to show the same word if user didn't learned it, but close the screen)
    var lastWordToLearnId: Int
        set(value) = preferences
                .edit()
                .putInt("last_word_to_learn_id", value)
                .apply()
        get() = preferences.getInt("last_word_to_learn_id", -1)

    // last word that was learned (to not repeat the same word two times in row)
    var lastLearnedWordId: Int
        set(value) = preferences
                .edit()
                .putInt("last_word_to_learn_id", value)
                .apply()
        get() = preferences.getInt("last_word_to_learn_id", -1)

}