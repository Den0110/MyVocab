package com.myvocab.domain.repositories

import com.myvocab.domain.entities.TranslatableText
import com.myvocab.domain.entities.Word

interface TranslationRepository {

    suspend fun translateInTranslator(text: TranslatableText): Word

    suspend fun translateInDictionary(text: TranslatableText): Word

}