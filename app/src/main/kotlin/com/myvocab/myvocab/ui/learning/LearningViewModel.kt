package com.myvocab.myvocab.ui.learning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.learning.GetLearningWordUseCase
import com.myvocab.myvocab.util.PreferencesManager
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LearningViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val getLearningWordUseCase: GetLearningWordUseCase,
        private val preferencesManager: PreferencesManager
) : ViewModel() {

    val currentWord: MutableLiveData<Word> = MutableLiveData()
    val wordSetTitle: MutableLiveData<String> = MutableLiveData()
    val showedWordNumber: MutableLiveData<Int> = MutableLiveData(preferencesManager.lastSessionShowedWordNumber)

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
                        val wordsShowed = showedWordNumber.value?.plus(1)
                        showedWordNumber.postValue(wordsShowed)
                        preferencesManager.lastSessionShowedWordNumber = wordsShowed ?: 0
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