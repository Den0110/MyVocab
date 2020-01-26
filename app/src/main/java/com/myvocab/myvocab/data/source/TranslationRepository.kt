package com.myvocab.myvocab.data.source

import com.myvocab.myvocab.BuildConfig
import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.data.model.TranslateUseCaseResult
import com.myvocab.myvocab.data.model.TranslationSource
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

    fun translateInTranslator(text: TranslatableText): Single<TranslateUseCaseResult> {
        return translatorApi
                .translate(BuildConfig.YANDEX_TRANSLATE_API_KEY, text.text, text.lang)
                .map {
                    TranslateUseCaseResult(text, it.text?.toList()
                            ?: arrayListOf(), TranslationSource.TRANSLATOR)
                }
    }

    fun translateInDictionary(text: TranslatableText): Single<TranslateUseCaseResult> {
        return dictionaryApi
                .translate(BuildConfig.YANDEX_DICTIONARY_API_KEY, text.text, text.lang)
                .map {
                    val translations = mutableListOf<String>()

                    translations.add(it.def?.get(0)?.tr?.get(0)?.text!!)
                    it.def?.get(0)?.tr?.get(0)?.syn?.forEach { syn ->
                        translations.add(syn.text)
                    }

                    TranslateUseCaseResult(text, translations, TranslationSource.DICTIONARY)
                }
    }

}