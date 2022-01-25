package com.myvocab.domain.usecases.my_word_sets

import com.myvocab.core.util.RepositoryData
import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.usecases.wordset.GetWordSetOptionsUseCaseResult
import com.myvocab.domain.usecases.wordset_details.GetWordSetUseCase

abstract class GetMyWordSetsUseCase (private val getWordSetUseCase: GetWordSetUseCase){

    suspend fun getWordSets(): List<GetWordSetOptionsUseCaseResult> {
        val wordSets = fetchWordSets()
        return wordSets.map { getWordSetUseCase.getWordSet(it.data.globalId) }
    }

    protected abstract suspend fun fetchWordSets(): List<RepositoryData<WordSet>>

}