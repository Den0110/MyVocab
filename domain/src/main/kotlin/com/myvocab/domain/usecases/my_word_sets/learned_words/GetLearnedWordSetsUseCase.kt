package com.myvocab.domain.usecases.my_word_sets.learned_words

import com.myvocab.core.util.RepositoryData
import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.repositories.WordSetRepository
import com.myvocab.domain.usecases.my_word_sets.GetMyWordSetsUseCase
import com.myvocab.domain.usecases.wordset_details.GetWordSetUseCase
import javax.inject.Inject

class GetLearnedWordSetsUseCase
@Inject
constructor(
    private val wordSetRepository: WordSetRepository,
    getWordSetUseCase: GetWordSetUseCase
) : GetMyWordSetsUseCase(getWordSetUseCase) {

    override suspend fun fetchWordSets(): List<RepositoryData<WordSet>> =
            wordSetRepository.getLearnedWordSets()

}