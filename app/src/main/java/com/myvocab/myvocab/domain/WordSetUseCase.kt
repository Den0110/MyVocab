package com.myvocab.myvocab.domain

import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.RepositoryData
import com.myvocab.myvocab.util.Source
import io.reactivex.Single
import javax.inject.Inject

class WordSetUseCase
@Inject
constructor(
        private val wordRepository: WordRepository
) {

    fun execute(parameter: RepositoryData<WordSet>): Single<WordSetUseCaseResult> =
            if (parameter.source == Source.LOCAL) {
                wordRepository
                        .getWordSetLearningPercentage(parameter.data.globalId)
                        .map { WordSetUseCaseResult(parameter.data, parameter.source == Source.LOCAL, it) }
            } else {
                Single.fromCallable { WordSetUseCaseResult(parameter.data, parameter.source == Source.LOCAL) }
            }

}
