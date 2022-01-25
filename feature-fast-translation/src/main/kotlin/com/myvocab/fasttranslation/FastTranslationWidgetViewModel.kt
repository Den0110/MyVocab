package com.myvocab.fasttranslation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myvocab.domain.common.Resource
import com.myvocab.domain.entities.TranslatableText
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.domain.usecases.translate.TranslateUseCase
import com.myvocab.domain.usecases.translate.TranslateUseCaseResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FastTranslationWidgetViewModel
@Inject
constructor(
    private val translateUseCase: TranslateUseCase,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _translateResult: MutableSharedFlow<Resource<TranslateUseCaseResult>> = MutableSharedFlow()
    val translateResult = _translateResult.asSharedFlow()

    fun translate(translatable: TranslatableText) {
        viewModelScope.launch {
            try {
                val translated = translateUseCase.execute(translatable)
                _translateResult.emit(Resource.Success(translated))
            } catch (e: Exception) {
                _translateResult.emit(Resource.Error(e))
            }
        }
    }

    suspend fun addToDictionary(translateResult: TranslateUseCaseResult) =
        wordRepository.addMyWord(translateResult.word)

}