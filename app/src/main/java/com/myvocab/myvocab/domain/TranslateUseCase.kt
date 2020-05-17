package com.myvocab.myvocab.domain

import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.data.model.TranslateUseCaseResult
import com.myvocab.myvocab.data.model.TranslationSource
import com.myvocab.myvocab.data.source.TranslationRepository
import com.myvocab.myvocab.data.source.WordRepository
import io.reactivex.Single
import javax.inject.Inject

class TranslateUseCase
@Inject
constructor(
        private val translationRepository: TranslationRepository,
        private val wordRepository: WordRepository
) {

    fun execute(translatableText: TranslatableText): Single<TranslateUseCaseResult> {
        val translate = if (translatableText.text.length < 50) {
            translationRepository
                    .translateInDictionary(translatableText)
                    .map {
                        val translations = mutableListOf<String>()

                        translations.add(it.def?.get(0)?.tr?.get(0)?.text!!)
                        it.def?.get(0)?.tr?.get(0)?.syn?.forEach { syn ->
                            translations.add(syn.text)
                        }

                        TranslateUseCaseResult(translatableText, translations, TranslationSource.DICTIONARY)
                    }
                    .onErrorResumeNext {
                        translationRepository
                                .translateInTranslator(translatableText)
                                .map {
                                    TranslateUseCaseResult(translatableText, it.text?.toList()
                                            ?: arrayListOf(), TranslationSource.TRANSLATOR)
                                }
                    }
        } else {
            translationRepository
                    .translateInTranslator(translatableText)
                    .map {
                        TranslateUseCaseResult(translatableText, it.text?.toList()
                                ?: arrayListOf(), TranslationSource.TRANSLATOR)
                    }
        }

        return wordRepository
                .getWordByContent(translatableText.text)
                .map {
                    TranslateUseCaseResult(translatableText, listOf(it.translation!!), TranslationSource.VOCAB)
                }
                .onErrorResumeNext { translate }
    }

}