package com.myvocab.myvocab.domain

import com.myvocab.myvocab.data.model.GetWordSetOptionsUseCaseResult
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.RepositoryData
import com.myvocab.myvocab.util.Source
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class GetWordSetOptionsUseCase
@Inject
constructor(
    private val wordRepository: WordRepository
) {

    suspend fun execute(parameter: RepositoryData<WordSet>): GetWordSetOptionsUseCaseResult {
        return when(parameter.source) {
            Source.LOCAL -> {
                wordRepository
                    .getWordSetLearningPercentage(parameter.data.globalId)
                    .map { GetWordSetOptionsUseCaseResult(parameter.data, true, it) }
                    .await()
            }
            Source.REMOTE -> {
                GetWordSetOptionsUseCaseResult(parameter.data, false)
            }
        }
    }

}
