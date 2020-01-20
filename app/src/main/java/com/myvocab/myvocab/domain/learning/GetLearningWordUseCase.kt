package com.myvocab.myvocab.domain.learning

import com.myvocab.myvocab.data.model.LearningWordUseCaseResult
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.GetNextWordToLearnUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetLearningWordUseCase
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val getNextWordToLearnUseCase: GetNextWordToLearnUseCase
) {

    fun execute(considerLastWordToLearn: Boolean): Single<LearningWordUseCaseResult> =
            getNextWordToLearnUseCase
                    .execute(considerLastWordToLearn)
                    .flatMap { word ->
                        wordRepository
                                .getWordSetEntityFromDb(word.wordSetId!!)
                                .map { LearningWordUseCaseResult(word, it.title) }
                    }

}