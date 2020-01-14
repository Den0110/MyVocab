package com.myvocab.myvocab.domain.word_set_details

import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.WordSetUseCase
import io.reactivex.Single
import javax.inject.Inject

class GetWordSetUseCase
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val wordSetUseCase: WordSetUseCase
) {

    fun getWordSet(parameter: String): Single<WordSetUseCaseResult> {
        return wordRepository
                .getWordSet(parameter)
                .flatMap { wordSetUseCase.execute(it) }
    }

}