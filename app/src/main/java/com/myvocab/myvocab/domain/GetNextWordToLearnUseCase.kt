package com.myvocab.myvocab.domain

import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.PreferencesManager
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import kotlin.random.Random

class GetNextWordToLearnUseCase
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val prefManager: PreferencesManager
) {

    companion object {
        const val UNKNOWN_LEVEL = 0
        const val RECENTLY_LEARNED_LEVEL = 1
        const val WELL_KNOWN_LEVEL = 2
    }

    private var currentWord: Word? = null
    private var lastWordId: Int? = null

    fun execute(considerLastWordToLearn: Boolean): Single<Word> =
            if (considerLastWordToLearn && currentWord == null && prefManager.lastWordToLearnId != -1) {
                wordRepository
                        .getWordById(prefManager.lastWordToLearnId)
                        .flatMap {
                            if (!it.needToLearn || it.knowingLevel > WELL_KNOWN_LEVEL) {
                                loadNextWord()
                            } else {
                                Single.fromCallable { it }
                            }
                        }
                        .onErrorResumeNext { loadNextWord() }
            } else {
                loadNextWord()
            }
                    .doOnSuccess { prefManager.lastWordToLearnId = it.id!! }
                    .doOnError { prefManager.lastWordToLearnId = -1 }


    private fun loadNextWord(): Single<Word> {
        val levels: List<Int> =
                when (Random.nextInt(100)) {
                    in 0..10 -> listOf(WELL_KNOWN_LEVEL, UNKNOWN_LEVEL, RECENTLY_LEARNED_LEVEL)
                    in 10..25 -> listOf(RECENTLY_LEARNED_LEVEL, UNKNOWN_LEVEL, WELL_KNOWN_LEVEL)
                    else -> listOf(UNKNOWN_LEVEL, RECENTLY_LEARNED_LEVEL, WELL_KNOWN_LEVEL)
                }
        return Observable.fromIterable(levels)
                .concatMapSingleDelayError { wordRepository.getInLearningWordsByKnowingLevel(it) }
                .takeUntil { it.isNotEmpty() }
                .filter { it.isNotEmpty() }
                .firstOrError()
                .map {
                    when {
                        it.size > 1 -> {
                            // любой из первых трех
                            var i: Int
                            do {
                                i = Random.nextInt(if (it.size < 3) it.size else 3)
                            } while (it[i].id == getLastLearnedWordId())
                            setLastLearnedWordId(it[i].id)
                            currentWord = it[i]
                            it[i]
                        }
                        it.size == 1 -> {
                            setLastLearnedWordId(it[0].id)
                            currentWord = it[0]
                            it[0]
                        }
                        else -> throw Exception("No new words to learn")
                    }
                }
    }

    private fun setLastLearnedWordId(id: Int?) {
        lastWordId = id
        prefManager.lastLearnedWordId = id ?: -1
    }

    private fun getLastLearnedWordId(): Int? {
        if (lastWordId == null && prefManager.lastLearnedWordId != -1)
            lastWordId = prefManager.lastLearnedWordId
        return lastWordId
    }

}