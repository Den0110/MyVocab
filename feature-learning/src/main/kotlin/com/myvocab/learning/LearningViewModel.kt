package com.myvocab.learning

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myvocab.core.util.PreferencesManager
import com.myvocab.domain.entities.Word
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.domain.usecases.learning.GetLearningWordUseCase
import kotlinx.coroutines.launch
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

    init {
        nextWord()
    }

    fun nextWord() {
        loadNextWord()
    }

    private fun loadNextWord() {
        viewModelScope.launch {
            try {
                val word = getLearningWordUseCase.execute(true)
                currentWord.postValue(word.word)
                wordSetTitle.postValue(word.wordSetTitle)
                val wordsShowed = showedWordNumber.value?.plus(1)
                showedWordNumber.postValue(wordsShowed)
                preferencesManager.lastSessionShowedWordNumber = wordsShowed ?: 0
            } catch (e: Exception) {
                currentWord.postValue(null)
                wordSetTitle.postValue(null)
            }
        }
    }

    suspend fun increaseKnowingLevel() {
        currentWord.value!!.apply {
            knowingLevel += 1
            lastShowTime = System.currentTimeMillis()
        }
        return wordRepository.updateWord(currentWord.value!!)
    }

    suspend fun zeroizeKnowingLevel() {
        currentWord.value!!.apply {
            knowingLevel = 0
            lastShowTime = System.currentTimeMillis()
        }
        return wordRepository.updateWord(currentWord.value!!)
    }

}