package com.myvocab.myvocab.ui.fast_translation

import androidx.lifecycle.*
import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.data.model.TranslateUseCaseResult
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.TranslateUseCase
import javax.inject.Inject

class FastTranslationWidgetViewModel
@Inject
constructor(
        private val translateUseCase: TranslateUseCase,
        private val wordRepository: WordRepository
) : ViewModel() {

    fun translate(translatable: TranslatableText) =
            translateUseCase.execute(translatable)

    fun addToDictionary(translateResult: TranslateUseCaseResult) =
            wordRepository.addMyWord(translateResult.word)

}