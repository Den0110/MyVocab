package com.myvocab.myvocab.ui.word_set_details

import android.os.Bundle
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.word_set_details.GetWordSetUseCase
import com.myvocab.myvocab.system.ResourceManager
import com.myvocab.myvocab.ui.word.BaseWordListViewModel
import com.myvocab.myvocab.util.Event
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class WordSetDetailsViewModel
@Inject
constructor(
    private val wordRepository: WordRepository,
    private val resources: ResourceManager,
    private val getWordSetUseCase: GetWordSetUseCase,
    data: Bundle
) : BaseWordListViewModel(wordRepository) {

    private val initialWordSet = WordSetDetailsFragmentArgs.fromBundle(data).wordSet

    private val _wordSet = MutableStateFlow<Resource<WordSet>>(Resource.Success(initialWordSet))

    val title: MediatorLiveData<String> = MediatorLiveData()
    val subtitle: MutableLiveData<String> = MutableLiveData()
    val isSavedLocally: MutableLiveData<Boolean> = MutableLiveData()

    val addingWordSet: MutableLiveData<Event<Resource<WordSet>>> = MutableLiveData()
    val removingWordSet: MutableLiveData<Event<Resource<WordSet>>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

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
        compositeDisposable.add(
            getWordSetUseCase
                .getWordSet(initialWordSet.globalId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    isSavedLocally.value = it.savedLocally
                    subtitle.value =
                        if (it.savedLocally) {
                            resources.getString(R.string.learning_percentage, it.learningPercentage)
                        } else {
                            resources
                                .getQuantityString(
                                    R.plurals.word_count,
                                    it.wordSet.words.size,
                                    it.wordSet.words.size
                                )
                        }
                    _wordSet.value = Resource.Success(it.wordSet)
                }, {
                    _wordSet.value = Resource.Error(it)
                })
        )
    }

    fun addWordSet() {
        if (_wordSet.value.data != null) {
            val ws = _wordSet.value.data!!
            addingWordSet.postValue(Event(Resource.Loading()))
            compositeDisposable.add(
                wordRepository
                    .addWordSet(ws)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        addingWordSet.postValue(Event(Resource.Success(ws)))
                    }, {
                        addingWordSet.postValue(Event(Resource.Error(it)))
                    })
            )
        }
    }

    fun removeWordSet() {
        if (_wordSet.value.data != null) {
            val ws = _wordSet.value.data!!
            removingWordSet.postValue(Event(Resource.Loading(ws)))
            compositeDisposable.add(
                wordRepository
                    .deleteWordSet(ws.globalId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        removingWordSet.postValue(Event(Resource.Success(ws)))
                    }, {
                        removingWordSet.postValue(Event(Resource.Error(it)))
                    })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}