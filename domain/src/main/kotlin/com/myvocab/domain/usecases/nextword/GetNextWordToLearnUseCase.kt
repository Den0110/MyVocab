package com.myvocab.domain.usecases.nextword

import com.myvocab.core.util.PreferencesManager
import com.myvocab.domain.entities.Word
import com.myvocab.domain.repositories.WordRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
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

    suspend fun execute(considerLastWordToLearn: Boolean): Word {
        try {
            val word = if (considerLastWordToLearn && currentWord == null && prefManager.lastWordToLearnId != -1) {
                try {
                    val w = wordRepository.getWordById(prefManager.lastWordToLearnId)

                    if (!w.needToLearn || w.knowingLevel > WELL_KNOWN_LEVEL) {
                        loadNextWord()
                    } else {
                        currentWord = w
                        w
                    }
                } catch (e: Exception) {
                    loadNextWord()
                }
            } else {
                loadNextWord()
            }
            prefManager.lastWordToLearnId = word.id!!
            return word
        } catch (e: Exception) {
            Timber.e(e)
            prefManager.lastWordToLearnId = -1
            throw e
        }
    }


    private suspend fun loadNextWord(): Word {
        val levels: List<Int> = when (Random.nextInt(100)) {
            in 0..10 -> listOf(WELL_KNOWN_LEVEL, UNKNOWN_LEVEL, RECENTLY_LEARNED_LEVEL)
            in 10..25 -> listOf(RECENTLY_LEARNED_LEVEL, UNKNOWN_LEVEL, WELL_KNOWN_LEVEL)
            else -> listOf(UNKNOWN_LEVEL, RECENTLY_LEARNED_LEVEL, WELL_KNOWN_LEVEL)
        }

        val words = levels
            .asFlow()
            .map { wordRepository.getInLearningWordsByKnowingLevel(it) }
            .first { it.isNotEmpty() }

        return when {
            words.size > 1 -> {
                // любой из первых трех
                var i: Int
                do {
                    i = Random.nextInt(if (words.size < 3) words.size else 3)
                } while (words[i].id == getLastLearnedWordId())
                setLastLearnedWordId(words[i].id)
                currentWord = words[i]
                words[i]
            }
            words.size == 1 -> {
                setLastLearnedWordId(words[0].id)
                currentWord = words[0]
                words[0]
            }
            else -> throw Exception("No new words to learn")
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