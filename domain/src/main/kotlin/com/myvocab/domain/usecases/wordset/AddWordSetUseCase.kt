package com.myvocab.domain.usecases.wordset

import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.domain.repositories.WordSetRepository
import javax.inject.Inject

class AddWordSetUseCase
@Inject
constructor(
    private val wordSetRepository: WordSetRepository,
    private val wordRepository: WordRepository
) {

    suspend fun invoke(wordSet: WordSet) {
        wordSetRepository.addWordSet(wordSet)

        wordSet.words.forEach {
            it.wordSetId = wordSet.globalId
        }

        wordRepository.addWords(wordSet.words)
    }
}