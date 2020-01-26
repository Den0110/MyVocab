package com.myvocab.myvocab.domain

import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.data.model.TranslateUseCaseResult
import com.myvocab.myvocab.data.source.TranslationRepository
import io.reactivex.Single
import javax.inject.Inject

class TranslateUseCase
@Inject
constructor(
        private val translationRepository: TranslationRepository
) {

    fun execute(translatableText: TranslatableText): Single<TranslateUseCaseResult> =
            if (translatableText.text.length < 50) {
                translationRepository
                        .translateInDictionary(translatableText)
                        .onErrorResumeNext {
                            translationRepository
                                    .translateInTranslator(translatableText)
                        }
            } else {
                translationRepository
                        .translateInTranslator(translatableText)
            }

}