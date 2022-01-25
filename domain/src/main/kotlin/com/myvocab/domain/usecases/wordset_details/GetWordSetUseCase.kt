package com.myvocab.domain.usecases.wordset_details

import com.myvocab.domain.repositories.WordSetRepository
import com.myvocab.domain.usecases.wordset.GetWordSetOptionsUseCase
import com.myvocab.domain.usecases.wordset.GetWordSetOptionsUseCaseResult
import javax.inject.Inject

class GetWordSetUseCase
@Inject
constructor(
    private val wordSetRepository: WordSetRepository,
    private val getWordSetOptionsUseCase: GetWordSetOptionsUseCase
) {

    suspend fun getWordSet(parameter: String): GetWordSetOptionsUseCaseResult {
        val wordSet = wordSetRepository.getWordSet(parameter)
        return getWordSetOptionsUseCase.execute(wordSet)
    }

}