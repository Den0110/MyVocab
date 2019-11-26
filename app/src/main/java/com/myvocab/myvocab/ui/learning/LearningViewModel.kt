package com.myvocab.myvocab.ui.learning

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.getLastLearnedWordId
import com.myvocab.myvocab.util.getLastWordToLearnId
import com.myvocab.myvocab.util.setLastLearnedWordId
import com.myvocab.myvocab.util.setLastWordToLearnId
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlin.random.Random

class LearningViewModel
@Inject
constructor(
        private val wordsRepository: WordRepository,
        private val application: Application
) : ViewModel() {

    companion object {
        const val TAG = "LearningViewModel"

        const val UNKNOWN_LEVEL = 0
        const val RECENTLY_LEARNED_LEVEL = 1
        const val WELL_KNOWN_LEVEL = 2
    }

    val currentWord: MutableLiveData<Word> = MutableLiveData()

    private var compositeDisposable = CompositeDisposable()
    private var lastWordId: Int? = null

    init {
        nextWord()
    }

    fun nextWord() {
        compositeDisposable.clear()
        compositeDisposable.add(
                if (currentWord.value == null && getLastWordToLearnId(application) != -1) {
                    wordsRepository.getWordById(getLastWordToLearnId(application)).subscribe({
                        if (it.knowingLevel > WELL_KNOWN_LEVEL) {
                            compositeDisposable.clear()
                            compositeDisposable.add(loadNextWord())
                        } else {
                            currentWord.postValue(it)
                            setLastWordToLearnId(application, it.id!!)
                        }
                    }, {
                        compositeDisposable.clear()
                        compositeDisposable.add(loadNextWord())
                    })
                } else {
                    loadNextWord()
                }
        )
    }

    private fun loadNextWord() =
            getNextWord().subscribe({
                currentWord.postValue(it)
                setLastWordToLearnId(application, it.id!!)
            }, {
                currentWord.postValue(null)
                setLastWordToLearnId(application, -1)
            })

    private fun getNextWord(): Single<Word> {
        val levels: List<Int> =
                when (Random.nextInt(100)) {
                    in 0..10 -> listOf(WELL_KNOWN_LEVEL, UNKNOWN_LEVEL, RECENTLY_LEARNED_LEVEL)
                    in 10..25 -> listOf(RECENTLY_LEARNED_LEVEL, UNKNOWN_LEVEL, WELL_KNOWN_LEVEL)
                    else -> listOf(UNKNOWN_LEVEL, RECENTLY_LEARNED_LEVEL, WELL_KNOWN_LEVEL)
                }
        return Observable.fromIterable(levels)
                .concatMapSingleDelayError { wordsRepository.getWordsByKnowingLevel(it) }
                .takeUntil { it.isNotEmpty() }
                .filter { it.isNotEmpty() }
                .firstOrError()
                .map {
                    when {
                        it.size > 1 -> {
                            var i: Int
                            do {
                                i = Random.nextInt(if (it.size < 3) it.size else 3)
                            } while (it[i].id == getLastLearnedWordId())
                            setLastLearnedWordId(it[i].id)
                            it[i]
                        }
                        it.size == 1 -> {
                            setLastLearnedWordId(it[0].id)
                            it[0]
                        }
                        else -> throw Exception("No new words to learn")
                    }
                }
    }

    fun increaseKnowingLevel(): Completable {
        currentWord.value!!.knowingLevel = currentWord.value!!.knowingLevel + 1
        currentWord.value!!.lastShowTime = System.currentTimeMillis()
        return wordsRepository.addWord(currentWord.value!!)
    }

    fun zeroizeKnowingLevel(): Completable {
        currentWord.value!!.knowingLevel = 0
        currentWord.value!!.lastShowTime = System.currentTimeMillis()
        return wordsRepository.addWord(currentWord.value!!)
    }

    fun getInLearningWordsCount() =
        wordsRepository.getInLearningWordsCount()

    private fun setLastLearnedWordId(id: Int?){
        lastWordId = id
        setLastLearnedWordId(application, id ?: -1)
    }

    private fun getLastLearnedWordId(): Int? {
        if (lastWordId == null && getLastLearnedWordId(application) != -1)
            lastWordId = getLastLearnedWordId(application)
        return lastWordId
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}