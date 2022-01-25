package com.myvocab.domain.usecases.search

import com.myvocab.core.util.RepositoryData
import com.myvocab.core.util.Source
import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.repositories.WordSetRepository
import com.myvocab.domain.usecases.wordset.GetWordSetOptionsUseCase
import com.myvocab.domain.usecases.wordset.GetWordSetOptionsUseCaseResult
import javax.inject.Inject

class GetSearchWordSetsUseCase
@Inject
constructor(
    private val wordSetRepository: WordSetRepository,
    private val getWordSetOptionsUseCase: GetWordSetOptionsUseCase
) {

    suspend fun getWordSets(): List<GetWordSetOptionsUseCaseResult> {
        return wordSetRepository.getAllWordSets()
            .map { getWordSetOptions(specifySource(it)) }
    }

    private suspend fun specifySource(wordSet: WordSet): RepositoryData<WordSet> {
        return if (wordSetRepository.isWordSetSavedLocally(wordSet.globalId)) {
            RepositoryData(wordSet, Source.LOCAL)
        } else {
            RepositoryData(wordSet, Source.REMOTE)
        }

    }

    private suspend fun getWordSetOptions(sourcedWordSet: RepositoryData<WordSet>) =
        getWordSetOptionsUseCase.execute(sourcedWordSet)

}