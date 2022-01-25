package com.myvocab.domain.usecases.wordset

import com.myvocab.core.util.RepositoryData
import com.myvocab.core.util.Source
import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.repositories.WordRepository
import javax.inject.Inject

class GetWordSetOptionsUseCase
@Inject
constructor(
    private val wordRepository: WordRepository
) {

    suspend fun execute(parameter: RepositoryData<WordSet>): GetWordSetOptionsUseCaseResult {
        return when(parameter.source) {
            Source.LOCAL -> {
                val percentage = wordRepository.getWordSetLearningPercentage(parameter.data.globalId)
                GetWordSetOptionsUseCaseResult(parameter.data, true, percentage)
            }
            Source.REMOTE -> {
                GetWordSetOptionsUseCaseResult(parameter.data, false)
            }
        }
    }

}
