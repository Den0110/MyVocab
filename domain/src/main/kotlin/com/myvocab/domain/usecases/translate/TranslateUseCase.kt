package com.myvocab.domain.usecases.translate

import com.myvocab.domain.entities.TranslatableText
import com.myvocab.domain.repositories.TranslationRepository
import com.myvocab.domain.repositories.WordRepository
import timber.log.Timber
import javax.inject.Inject

class TranslateUseCase
@Inject
constructor(
    private val translationRepository: TranslationRepository,
    private val wordRepository: WordRepository
) {

    suspend fun execute(text: TranslatableText): TranslateUseCaseResult {
        return try {
            translateInVocab(text)
        } catch (e: Exception) {
            if (text.text.length < 50) {
                try {
                    translateInDictionary(text)
                } catch (e: Exception) {
                    Timber.e(e)
                    translateInTranslator(text)
                }
            } else {
                translateInTranslator(text)
            }
        }
    }

    private suspend fun translateInVocab(text: TranslatableText): TranslateUseCaseResult {
        val word = wordRepository.getWordByContent(text.text)
        return TranslateUseCaseResult(text, word, TranslationSource.VOCAB)
    }

    private suspend fun translateInDictionary(text: TranslatableText): TranslateUseCaseResult {
        val word = translationRepository.translateInDictionary(text)
        if (word.isEmpty()) throw RuntimeException("Failed to find the word in dictionary")
        return TranslateUseCaseResult(text, word, TranslationSource.DICTIONARY)
    }

    private suspend fun translateInTranslator(text: TranslatableText): TranslateUseCaseResult {
        val word = translationRepository.translateInTranslator(text)
        return TranslateUseCaseResult(text, word, TranslationSource.TRANSLATOR)
    }

}