package com.myvocab.domain.usecases.translate

import android.os.Parcelable
import com.myvocab.domain.entities.TranslatableText
import com.myvocab.domain.entities.Word
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class TranslateUseCaseResult(
    val text: TranslatableText,
    val word: Word,
    var source: TranslationSource
) : Parcelable, Serializable

enum class TranslationSource { TRANSLATOR, DICTIONARY, VOCAB }