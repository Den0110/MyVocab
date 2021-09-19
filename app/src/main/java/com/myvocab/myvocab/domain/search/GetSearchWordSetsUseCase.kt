package com.myvocab.myvocab.domain.search

import com.myvocab.myvocab.data.model.GetWordSetOptionsUseCaseResult
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.GetWordSetOptionsUseCase
import com.myvocab.myvocab.util.RepositoryData
import com.myvocab.myvocab.util.Source
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class GetSearchWordSetsUseCase
@Inject
constructor(
    private val wordRepository: WordRepository,
    private val getWordSetOptionsUseCase: GetWordSetOptionsUseCase
) {

    suspend fun getWordSets(): List<GetWordSetOptionsUseCaseResult> {
        return wordRepository.getWordSetsFromServer()
            .map { getWordSetOptions(specifySource(it)) }
    }

    private suspend fun specifySource(wordSet: WordSet): RepositoryData<WordSet> {
        return wordRepository
            .isWordSetSavedLocally(wordSet.globalId)
            .map {
                if (it) {
                    RepositoryData(wordSet, Source.LOCAL)
                } else {
                    RepositoryData(wordSet, Source.REMOTE)
                }
            }.await()
    }

    private suspend fun getWordSetOptions(sourcedWordSet: RepositoryData<WordSet>) =
        getWordSetOptionsUseCase.execute(sourcedWordSet)

}