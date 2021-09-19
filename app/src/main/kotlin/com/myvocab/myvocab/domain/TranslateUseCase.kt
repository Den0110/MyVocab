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
                    .flatMap {
                        if(it.isEmpty()) {
                            translationRepository
                                    .translateInTranslator(translatableText)
                                    .map {
                                        TranslateUseCaseResult(translatableText, it, TranslationSource.TRANSLATOR)
                                    }
                        } else {
                            Single.just(TranslateUseCaseResult(translatableText, it, TranslationSource.DICTIONARY))
                        }
                    }
        } else {
            translationRepository
                    .translateInTranslator(translatableText)
                    .map {
                        TranslateUseCaseResult(translatableText, it, TranslationSource.TRANSLATOR)
                    }
        }

        return wordRepository
                .getWordByContent(translatableText.text)
                .map {
                    TranslateUseCaseResult(translatableText, it, TranslationSource.VOCAB)
                }
                .onErrorResumeNext { translate }
    }

}