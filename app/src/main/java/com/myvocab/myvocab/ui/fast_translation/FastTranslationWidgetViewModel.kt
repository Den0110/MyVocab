package com.myvocab.myvocab.ui.fast_translation

import androidx.lifecycle.*
import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.data.model.TranslateUseCaseResult
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.TranslateUseCase
import io.reactivex.Single
import javax.inject.Inject

class FastTranslationWidgetViewModel
@Inject
constructor(
        private val translateUseCase: TranslateUseCase,
        private val wordRepository: WordRepository
) : ViewModel() {

    fun translate(translatable: TranslatableText): Single<TranslateUseCaseResult> {
        return translateUseCase.execute(translatable)
    }

    fun addToDictionary(translateResult: TranslateUseCaseResult) {
        wordRepository
                .addMyWord(
                    Word(
                        word = translateResult.text.text,
                        translation = translateResult.translations[0]
                    )
                )
                .subscribe()
    }

}