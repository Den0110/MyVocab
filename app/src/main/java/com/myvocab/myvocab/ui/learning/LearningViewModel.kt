package com.myvocab.myvocab.ui.learning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.learning.GetLearningWordUseCase
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LearningViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val getLearningWordUseCase: GetLearningWordUseCase
) : ViewModel() {

    val currentWord: MutableLiveData<Word> = MutableLiveData()
    val wordSetTitle: MutableLiveData<String> = MutableLiveData()

    private var compositeDisposable = CompositeDisposable()

    init {
        nextWord()
    }

    fun nextWord() {
        compositeDisposable.clear()
        compositeDisposable.add(loadNextWord())
    }

    private fun loadNextWord() =
            getLearningWordUseCase
                    .execute(true)
                    .subscribe({
                        currentWord.postValue(it.word)
                        wordSetTitle.postValue(it.wordSetTitle)
                    }, {
                        currentWord.postValue(null)
                        wordSetTitle.postValue(null)
                    })

    fun increaseKnowingLevel(): Completable {
        currentWord.value!!.apply {
            knowingLevel += 1
            lastShowTime = System.currentTimeMillis()
        }
        return wordRepository.updateWord(currentWord.value!!)
    }

    fun zeroizeKnowingLevel(): Completable {
        currentWord.value!!.apply {
            knowingLevel = 0
            lastShowTime = System.currentTimeMillis()
        }
        return wordRepository.updateWord(currentWord.value!!)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}