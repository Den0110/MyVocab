package com.myvocab.myvocab.data.model

data class TranslateUseCaseResult(
        val text: TranslatableText,
        val translations: List<String>,
        var source: TranslationSource
)

enum class TranslationSource { TRANSLATOR, DICTIONARY }