package com.myvocab.myvocab.util

import android.content.Context

const val APP_PREFERENCES = "APP_PREFERENCES"

fun setFastTranslationState(context: Context, state: Boolean) =
    context
            .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putBoolean("fast_translation_state", state)
            .apply()

fun getFastTranslationState(context: Context): Boolean =
    context
            .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean("fast_translation_state", false)

fun setLastWordToLearnId(context: Context, id: Int) =
        context
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putInt("last_word_to_learn_id", id)
                .apply()

fun getLastWordToLearnId(context: Context): Int =
        context
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .getInt("last_word_to_learn_id", -1)

fun setLastLearnedWordId(context: Context, id: Int) =
        context
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putInt("last_word_to_learn_id", id)
                .apply()

fun getLastLearnedWordId(context: Context): Int =
        context
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .getInt("last_word_to_learn_id", -1)