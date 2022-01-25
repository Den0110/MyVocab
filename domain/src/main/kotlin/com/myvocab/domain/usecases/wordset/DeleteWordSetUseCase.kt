package com.myvocab.domain.usecases.wordset

import com.myvocab.domain.repositories.WordRepository
import com.myvocab.domain.repositories.WordSetRepository
import javax.inject.Inject

class DeleteWordSetUseCase
@Inject
constructor(
    private val wordSetRepository: WordSetRepository,
    private val wordRepository: WordRepository
) {

    suspend fun invoke(globalId: String) {
        wordSetRepository.deleteWordSet(globalId)
        wordRepository.deleteWordsByWordSetId(globalId)
    }
}