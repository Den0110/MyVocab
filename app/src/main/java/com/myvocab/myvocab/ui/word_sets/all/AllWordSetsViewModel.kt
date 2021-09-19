package com.myvocab.myvocab.ui.word_sets.all

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myvocab.myvocab.data.model.GetWordSetOptionsUseCaseResult
import com.myvocab.myvocab.domain.search.GetSearchWordSetsUseCase
import com.myvocab.myvocab.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllWordSetsViewModel
@Inject
constructor(
    private val getSearchWordSetsUseCase: GetSearchWordSetsUseCase
) : ViewModel() {

    val getWordSetsOptions: MutableLiveData<Resource<List<GetWordSetOptionsUseCaseResult>>> = MutableLiveData()

    init {
        loadWordSets()
    }

    fun loadWordSets() {
        viewModelScope.launch {
            try {
                getWordSetsOptions.postValue(Resource.Loading())
                getWordSetsOptions.postValue(Resource.Success(getSearchWordSetsUseCase.getWordSets()))
            } catch (e: Exception) {
                getWordSetsOptions.postValue(Resource.Error(e))
            }
        }
    }

}