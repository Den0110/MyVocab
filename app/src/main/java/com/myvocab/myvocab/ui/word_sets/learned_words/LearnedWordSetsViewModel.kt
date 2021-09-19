package com.myvocab.myvocab.ui.word_sets.learned_words

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myvocab.myvocab.data.model.GetWordSetOptionsUseCaseResult
import com.myvocab.myvocab.domain.my_word_sets.learned_words.GetLearnedWordSetsUseCase
import com.myvocab.myvocab.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class LearnedWordSetsViewModel
@Inject
constructor(
    private val learnedWordSetsUseCase: GetLearnedWordSetsUseCase
) : ViewModel() {

    var getWordSetsOptions: MutableLiveData<Resource<List<GetWordSetOptionsUseCaseResult>>> = MutableLiveData()

    init {
        loadLearnedWords()
    }

    fun loadLearnedWords() {
        viewModelScope.launch {
            try {
                val results = learnedWordSetsUseCase.getWordSets()
//                // there is no sense to show 100% learned status for all word sets
//                // so, null learningPercentage for all ones
//                it.forEach { ws -> ws.learningPercentage = null }
                getWordSetsOptions.postValue(Resource.Success(results))
            } catch (e: Exception) {
                getWordSetsOptions.postValue(Resource.Error(e))
            }
        }
    }

}