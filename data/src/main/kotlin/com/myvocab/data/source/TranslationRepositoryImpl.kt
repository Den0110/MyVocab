package com.myvocab.data.source

import com.myvocab.core.BuildConfig
import com.myvocab.data.mappers.fromNetworkDictionaryModel
import com.myvocab.data.mappers.fromNetworkTranslatorModel
import com.myvocab.data.source.remote.translation.DictionaryApi
import com.myvocab.data.source.remote.translation.TranslatorApi
import com.myvocab.domain.entities.TranslatableText
import com.myvocab.domain.entities.Word
import com.myvocab.domain.repositories.TranslationRepository
import javax.inject.Inject

class TranslationRepositoryImpl
@Inject
constructor(
    private val translatorApi: TranslatorApi,
    private val dictionaryApi: DictionaryApi
) : TranslationRepository {

    override suspend fun translateInTranslator(text: TranslatableText): Word {
        val word = translatorApi.translate(BuildConfig.YANDEX_TRANSLATE_API_KEY, text.text, text.lang)
        return fromNetworkTranslatorModel().map(word).copy(word = text.text)
    }


    override suspend fun translateInDictionary(text: TranslatableText): Word {
        val word = dictionaryApi.translate(BuildConfig.YANDEX_DICTIONARY_API_KEY, text.text, text.lang)
        return fromNetworkDictionaryModel().map(word)
    }

}