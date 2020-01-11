package com.myvocab.myvocab.util

import android.content.Context
import android.content.SharedPreferences
import com.myvocab.myvocab.common.ReminderScheduler
import com.myvocab.myvocab.common.moveTimeToNextDayIfNeeded
import java.util.*

class PreferencesManager(context: Context) {

    companion object {
        const val APP_PREFERENCES = "APP_PREFERENCES"
    }

    private val preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    /*
    *   Fast Translation
    */

    fun setFastTranslationState(state: Boolean) =
            preferences
                    .edit()
                    .putBoolean("fast_translation_state", state)
                    .apply()

    fun getFastTranslationState(): Boolean =
            preferences.getBoolean("fast_translation_state", false)

    /*
    *   Reminder
    */

    fun setRemindingState(state: Boolean) =
            preferences
                    .edit()
                    .putBoolean("reminding_state", state)
                    .apply()

    fun getRemindingState(): Boolean =
            preferences.getBoolean("reminding_state", true)

    fun setRemindOnlyWordsToLearn(state: Boolean) =
            preferences
                    .edit()
                    .putBoolean("remind_only_words_to_learn", state)
                    .apply()

    fun getRemindOnlyWordsToLearn(): Boolean =
            preferences.getBoolean("remind_only_words_to_learn", false)

    fun setRemindingTime(time: Long) =
            preferences
                    .edit()
                    .putLong("reminding_time", time)
                    .apply()

    fun getRemindingTime(): Long {
        val millis = preferences
                .getLong("reminding_time", ReminderScheduler.REMINDER_DEFAULT_TIME)
        return Calendar.getInstance().apply {
            timeInMillis = millis
            moveTimeToNextDayIfNeeded()
        }.timeInMillis
    }

    /*
    *   Learning
    */

    fun setLastWordToLearnId(id: Int) =
            preferences
                    .edit()
                    .putInt("last_word_to_learn_id", id)
                    .apply()

    // last word that was chosen (to show the same word if user didn't learned it, but close the screen)
    fun getLastWordToLearnId(): Int =
            preferences.getInt("last_word_to_learn_id", -1)

    fun setLastLearnedWordId(id: Int) =
            preferences
                    .edit()
                    .putInt("last_word_to_learn_id", id)
                    .apply()

    // last word that was learned (to not repeat the same word two times in row)
    fun getLastLearnedWordId(): Int =
            preferences.getInt("last_word_to_learn_id", -1)

}