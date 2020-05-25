package com.myvocab.myvocab.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TranslateUseCaseResult(
        val text: TranslatableText,
        val word: Word,
        var source: TranslationSource
) : Parcelable

enum class TranslationSource { TRANSLATOR, DICTIONARY, VOCAB }