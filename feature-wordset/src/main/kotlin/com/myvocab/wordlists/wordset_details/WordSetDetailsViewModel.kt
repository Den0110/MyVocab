package com.myvocab.wordlists.wordset_details

import android.os.Bundle
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myvocab.core.system.ResourceManager
import com.myvocab.core.util.Event
import com.myvocab.domain.common.Resource
import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.domain.usecases.wordset.AddWordSetUseCase
import com.myvocab.domain.usecases.wordset.DeleteWordSetUseCase
import com.myvocab.domain.usecases.wordset_details.GetWordSetUseCase
import com.myvocab.wordlists.BaseWordListViewModel
import com.myvocab.wordlists.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class WordSetDetailsViewModel
@Inject
constructor(
    wordRepository: WordRepository,
    private val resources: ResourceManager,
    private val getWordSetUseCase: GetWordSetUseCase,
    private val addWordSetUseCase: AddWordSetUseCase,
    private val deleteWordSetUseCase: DeleteWordSetUseCase,
    data: Bundle
) : BaseWordListViewModel(wordRepository) {

    private val initialWordSet = WordSetDetailsFragmentArgs.fromBundle(data).wordSet

    private val _wordSet = MutableStateFlow<Resource<WordSet>>(Resource.Success(initialWordSet))

    val title: MediatorLiveData<String> = MediatorLiveData()
    val subtitle: MutableLiveData<String> = MutableLiveData()
    val isSavedLocally: MutableLiveData<Boolean> = MutableLiveData()

    val addingWordSet: MutableLiveData<Event<Resource<WordSet>>> = MutableLiveData()
    val removingWordSet: MutableLiveData<Event<Resource<WordSet>>> = MutableLiveData()

    init {
        viewModelScope.launch {
            _wordSet.collectLatest {
                _words.emit(it.withNewData(it.data?.words?.toMutableList()))
                title.postValue(it.data?.title)
            }
        }

        loadWordSet()
    }

    fun loadWordSet() {
        _wordSet.value = Resource.Loading(initialWordSet)
        viewModelScope.launch {
            try {
                val result = getWordSetUseCase.getWordSet(initialWordSet.globalId)
                isSavedLocally.value = result.savedLocally
                subtitle.postValue(
                    if (result.savedLocally) {
                        resources.getString(R.string.learning_percentage, result.learningPercentage)
                    } else {
                        resources.getQuantityString(
                            R.plurals.word_count,
                            result.wordSet.words.size,
                            result.wordSet.words.size
                        )
                    }
                )
                _wordSet.emit(Resource.Success(result.wordSet))
            } catch (e: Exception) {
                _wordSet.emit(Resource.Error(e))
            }
        }
    }

    fun addWordSet() {
        if (_wordSet.value.data != null) {
            val ws = _wordSet.value.data!!
            addingWordSet.postValue(Event(Resource.Loading()))
            viewModelScope.launch {
                try {
                    addWordSetUseCase.invoke(ws)
                    addingWordSet.postValue(Event(Resource.Success(ws)))
                } catch (e: Exception) {
                    addingWordSet.postValue(Event(Resource.Error(e)))
                }
            }
        }
    }

    fun removeWordSet() {
        if (_wordSet.value.data != null) {
            val ws = _wordSet.value.data!!
            removingWordSet.postValue(Event(Resource.Loading(ws)))
            viewModelScope.launch {
                try {
                    deleteWordSetUseCase.invoke(ws.globalId)
                    removingWordSet.postValue(Event(Resource.Success(ws)))
                } catch (e: Exception) {
                    removingWordSet.postValue(Event(Resource.Error(e)))
                }
            }
        }
    }

}