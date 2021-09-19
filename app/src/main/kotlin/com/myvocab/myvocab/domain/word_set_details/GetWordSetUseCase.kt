package com.myvocab.myvocab.domain.word_set_details

import com.myvocab.myvocab.data.model.GetWordSetOptionsUseCaseResult
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.GetWordSetOptionsUseCase
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class GetWordSetUseCase
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val getWordSetOptionsUseCase: GetWordSetOptionsUseCase
) {

    suspend fun getWordSet(parameter: String): GetWordSetOptionsUseCaseResult {
        val wordSet = wordRepository.getWordSet(parameter).await()
        return getWordSetOptionsUseCase.execute(wordSet)
    }

}