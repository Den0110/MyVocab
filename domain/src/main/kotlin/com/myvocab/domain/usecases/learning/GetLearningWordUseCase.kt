package com.myvocab.domain.usecases.learning

import com.myvocab.domain.repositories.WordSetRepository
import com.myvocab.domain.usecases.nextword.GetNextWordToLearnUseCase
import javax.inject.Inject

class GetLearningWordUseCase
@Inject
constructor(
    private val wordSetRepository: WordSetRepository,
    private val getNextWordToLearnUseCase: GetNextWordToLearnUseCase
) {

    suspend fun execute(considerLastWordToLearn: Boolean): LearningWordUseCaseResult {
        val word = getNextWordToLearnUseCase.execute(considerLastWordToLearn)
        val wordSet = wordSetRepository.getWordSet(word.wordSetId!!)
        return LearningWordUseCaseResult(word, wordSet.data.title)
    }

}