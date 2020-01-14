package com.myvocab.myvocab.ui.word_set_details

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.domain.word_set_details.GetWordSetUseCase
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
) : ViewModel() {

    private val initialWordSet = WordSetDetailsFragmentArgs.fromBundle(data).wordSet

    val wordSet: MutableLiveData<Resource<WordSet>> = MutableLiveData(Resource.success(initialWordSet))
    val isSavedLocally: MutableLiveData<Boolean> = MutableLiveData()
    val subtitle: MutableLiveData<String> = MutableLiveData()

    val addingWordSet: MutableLiveData<Resource<WordSet>> = MutableLiveData()
    val removingWordSet: MutableLiveData<Resource<WordSet>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    init {
        loadWordSet()
    }

    fun loadWordSet() {
        wordSet.value = Resource.loading()
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
                            wordSet.value = Resource.success(it.wordSet)
                        }, {
                            wordSet.value = Resource.error(it)
                        })
        )
    }

    fun addWordSet() {
        if (wordSet.value?.data != null) {
            val ws = wordSet.value!!.data!!
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
        if (wordSet.value?.data != null) {
            val ws = wordSet.value!!.data!!
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
        compositeDisposable.clear()
    }

}