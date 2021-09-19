package com.myvocab.myvocab.domain.my_word_sets

import com.myvocab.myvocab.data.model.GetWordSetOptionsUseCaseResult
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.domain.word_set_details.GetWordSetUseCase
import com.myvocab.myvocab.util.RepositoryData
import io.reactivex.Single
import kotlinx.coroutines.rx2.await

abstract class GetMyWordSetsUseCase (private val getWordSetUseCase: GetWordSetUseCase){

    suspend fun getWordSets(): List<GetWordSetOptionsUseCaseResult> {
        val wordSets = fetchWordSets().await()
        return wordSets.map { getWordSetUseCase.getWordSet(it.data.globalId) }
    }

    protected abstract fun fetchWordSets(): Single<List<RepositoryData<WordSet>>>

}