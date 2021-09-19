package com.myvocab.myvocab.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class TranslateUseCaseResult(
        val text: TranslatableText,
        val word: Word,
        var source: TranslationSource
) : Parcelable, Serializable

enum class TranslationSource { TRANSLATOR, DICTIONARY, VOCAB }