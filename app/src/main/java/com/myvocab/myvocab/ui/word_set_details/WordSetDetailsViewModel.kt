package com.myvocab.myvocab.ui.word_set_details

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.word_set_details.GetWordSetUseCase
import com.myvocab.myvocab.ui.word.BaseWordListViewModel
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class WordSetDetailsViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        private val getWordSetUseCase: GetWordSetUseCase,
        data: Bundle,
        private val context: Context
) : BaseWordListViewModel(wordRepository) {

    private val initialWordSet = WordSetDetailsFragmentArgs.fromBundle(data).wordSet

    private val _wordSet: MutableLiveData<Resource<WordSet>> = MutableLiveData(Resource.success(initialWordSet))

    val title: MediatorLiveData<String> = MediatorLiveData()
    val subtitle: MutableLiveData<String> = MutableLiveData()
    val isSavedLocally: MutableLiveData<Boolean> = MutableLiveData()

    val addingWordSet: MutableLiveData<Resource<WordSet>> = MutableLiveData()
    val removingWordSet: MutableLiveData<Resource<WordSet>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    init {
        words.addSource(_wordSet) {
            words.value = Resource(it.status, it.data?.words?.toMutableList(), it.error)
        }

        title.addSource(_wordSet) {
            title.value = it.data?.title
        }

        loadWordSet()
    }

    fun loadWordSet() {
        _wordSet.value = Resource.loading(initialWordSet)
        compositeDisposable.add(
                getWordSetUseCase
                        .getWordSet(initialWordSet.globalId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            isSavedLocally.value = it.savedLocally
                            subtitle.value =
                                    if (it.savedLocally) {
                                        context.getString(R.string.learning_percentage, it.learningPercentage)
                                    } else {
                                        context
                                                .resources
                                                .getQuantityString(
                                                        R.plurals.word_count,
                                                        it.wordSet.words.size,
                                                        it.wordSet.words.size
                                                )
                                    }
                            _wordSet.value = Resource.success(it.wordSet)
                        }, {
                            _wordSet.value = Resource.error(it)
                        })
        )
    }

    fun addWordSet() {
        if (_wordSet.value?.data != null) {
            val ws = _wordSet.value!!.data!!
            addingWordSet.postValue(Resource.loading())
            compositeDisposable.add(
                    wordRepository
                            .addWordSet(ws)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                addingWordSet.postValue(Resource.success(ws))
                            }, {
                                addingWordSet.postValue(Resource.error(it))
                            })
            )
        }
    }

    fun removeWordSet() {
        if (_wordSet.value?.data != null) {
            val ws = _wordSet.value!!.data!!
            removingWordSet.postValue(Resource.loading(ws))
            compositeDisposable.add(
                    wordRepository
                            .deleteWordSet(ws.globalId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                removingWordSet.postValue(Resource.success(ws))
                            }, {
                                removingWordSet.postValue(Resource.error(it))
                            })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}