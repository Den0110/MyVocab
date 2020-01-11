package com.myvocab.myvocab.ui.learning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.learning.GetNextWordToLearnUseCase
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LearningViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val getNextWordUseCase: GetNextWordToLearnUseCase
) : ViewModel() {

    val currentWord: MutableLiveData<Word> = MutableLiveData()

    private var compositeDisposable = CompositeDisposable()

    init {
        nextWord()
    }

    fun nextWord() {
        compositeDisposable.clear()
        compositeDisposable.add(loadNextWord())
    }

    private fun loadNextWord() =
            getNextWordUseCase.execute(true).subscribe({
                currentWord.postValue(it)
            }, {
                currentWord.postValue(null)
            })

    fun increaseKnowingLevel(): Completable {
        currentWord.value!!.knowingLevel = currentWord.value!!.knowingLevel + 1
        currentWord.value!!.lastShowTime = System.currentTimeMillis()
        return wordRepository.addWord(currentWord.value!!)
    }

    fun zeroizeKnowingLevel(): Completable {
        currentWord.value!!.knowingLevel = 0
        currentWord.value!!.lastShowTime = System.currentTimeMillis()
        return wordRepository.addWord(currentWord.value!!)
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}