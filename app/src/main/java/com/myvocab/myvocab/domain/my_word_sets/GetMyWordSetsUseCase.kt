package com.myvocab.myvocab.domain.my_word_sets

import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.domain.WordSetUseCase
import com.myvocab.myvocab.domain.word_set_details.GetWordSetUseCase
import com.myvocab.myvocab.util.RepositoryData
import io.reactivex.Single

abstract class GetMyWordSetsUseCase (private val getWordSetUseCase: GetWordSetUseCase){

    fun getWordSets(): Single<List<WordSetUseCaseResult>> {
        return fetchWordSets()
                .toObservable()
                .flatMapIterable { it }
                .flatMapSingle { getWordSetUseCase.getWordSet(it.data.globalId) }
                .toSortedList()
    }

    protected abstract fun fetchWordSets(): Single<List<RepositoryData<WordSet>>>

}