package com.myvocab.myvocab.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.myvocab.myvocab.common.ReminderScheduler
import java.util.*

class PreferencesManager(val context: Context) {

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
        set(value) {
            if(value != fastTranslationState) {
                if (value) {
                    // log enabling fast translation
                    FirebaseAnalytics.getInstance(context).logEvent("fast_translation_enabled", Bundle())
                } else {
                    // log disabling fast translation
                    FirebaseAnalytics.getInstance(context).logEvent("fast_translation_disabled", Bundle())
                }
                preferences
                        .edit()
                        .putBoolean("fast_translation_state", value)
                        .apply()
            }
        }
        get() = preferences.getBoolean("fast_translation_state", true)

    /**
     *  Reminder
     */

    var remindingState: Boolean
        set(value) {
            if (value != remindingState) {
                if (value) {
                    // log enabling reminder
                    FirebaseAnalytics.getInstance(context).logEvent("reminder_enabled", Bundle().apply {
                        putString("time", Date(remindingTime).toString())
                    })
                } else {
                    // log disabling reminder
                    FirebaseAnalytics.getInstance(context).logEvent("reminder_disabled", Bundle().apply {
                        putString("time", Date(remindingTime).toString())
                    })
                }
                preferences
                        .edit()
                        .putBoolean("reminding_state", value)
                        .apply()
            }
        }
        get() = preferences.getBoolean("reminding_state", true)

    var remindOnlyWordsToLearn: Boolean
        set(value) {
            if (value != remindOnlyWordsToLearn) {
                if (value) {
                    // log enabling remind only words to learn
                    FirebaseAnalytics.getInstance(context).logEvent("reminder_learn_words_enabled", Bundle().apply {
                        putString("time", Date(remindingTime).toString())
                    })
                } else {
                    // log disabling remind only words to learn
                    FirebaseAnalytics.getInstance(context).logEvent("reminder_learn_words_disabled", Bundle().apply {
                        putString("time", Date(remindingTime).toString())
                    })
                }
                preferences
                        .edit()
                        .putBoolean("remind_only_words_to_learn", value)
                        .apply()
            }
        }
        get() = preferences.getBoolean("remind_only_words_to_learn", false)

    var remindingTime: Long
        set(value) {
            if (value != remindingTime) {
                // log changing reminder time
                FirebaseAnalytics.getInstance(context).logEvent("reminder_time_changed", Bundle().apply {
                    putString("new_time", Date(value).toString())
                })
                preferences
                        .edit()
                        .putLong("reminding_time", value)
                        .apply()
            }
        }
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

    /**
     *  Ads
     */

    var lastSessionShowedWordNumber: Int
        set(value) = preferences
                .edit()
                .putInt("last_session_showed word_number", value)
                .apply()
        get() = preferences.getInt("last_session_showed word_number", 0)

}