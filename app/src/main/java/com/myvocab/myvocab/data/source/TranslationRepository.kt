package com.myvocab.myvocab.data.source

import com.myvocab.myvocab.BuildConfig
import com.myvocab.myvocab.data.model.*
import com.myvocab.myvocab.data.source.remote.translation.DictionaryApi
import com.myvocab.myvocab.data.source.remote.translation.TranslatorApi
import io.reactivex.Single
import javax.inject.Inject

class TranslationRepository
@Inject
constructor(
        private val translatorApi: TranslatorApi,
        private val dictionaryApi: DictionaryApi
){

    fun translateInTranslator(text: TranslatableText): Single<TranslatorModel> {
        return translatorApi
                .translate(BuildConfig.YANDEX_TRANSLATE_API_KEY, text.text, text.lang)
    }

    fun translateInDictionary(text: TranslatableText): Single<DictionaryModel> {
        return dictionaryApi
                .translate(BuildConfig.YANDEX_DICTIONARY_API_KEY, text.text, text.lang)
    }

}