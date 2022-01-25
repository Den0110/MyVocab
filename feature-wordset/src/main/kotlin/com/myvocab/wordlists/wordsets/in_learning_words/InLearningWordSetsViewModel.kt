package com.myvocab.wordlists.wordsets.in_learning_words

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myvocab.domain.common.Resource
import com.myvocab.domain.usecases.my_word_sets.in_learning_words.GetInLearningWordSetsUseCase
import com.myvocab.domain.usecases.wordset.GetWordSetOptionsUseCaseResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class InLearningWordSetsViewModel
@Inject
constructor(
    private val inLearningWordSetsUseCase: GetInLearningWordSetsUseCase
) : ViewModel() {

    var getWordSetsOptions: MutableLiveData<Resource<List<GetWordSetOptionsUseCaseResult>>> = MutableLiveData()

    init {
        loadInLearningWords()
    }

    fun loadInLearningWords() {
        viewModelScope.launch {
            try {
                val results = inLearningWordSetsUseCase.getWordSets()
                getWordSetsOptions.postValue(Resource.Success(results))
            } catch (e: Exception) {
                getWordSetsOptions.postValue(Resource.Error(e))
            }
        }
    }

}