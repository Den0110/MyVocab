package com.myvocab.myvocab.domain.word_set_details

import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.UseCase
import com.myvocab.myvocab.util.Source
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

open class LoadWordSetUseCase
@Inject
constructor(
        private val wordRepository: WordRepository
) : UseCase<String, LoadWordSetUseCaseResult>() {

    override fun execute(parameter: String): Observable<LoadWordSetUseCaseResult> {
        return wordRepository
                .getWordSet(parameter)
                .flatMap { repData ->
                    if(repData.source == Source.LOCAL){
                        wordRepository
                                .getWordSetLearningPercentage(parameter)
                                .map { LoadWordSetUseCaseResult(repData.data, repData.source == Source.LOCAL, it) }
                    } else {
                        Single.fromCallable { LoadWordSetUseCaseResult(repData.data, repData.source == Source.LOCAL) }
                    }
                }
                .toObservable()
    }

}

data class LoadWordSetUseCaseResult(
        val wordSet: WordSet,
        val savedLocally: Boolean,
        val learningPercentage: Int? = null
)
