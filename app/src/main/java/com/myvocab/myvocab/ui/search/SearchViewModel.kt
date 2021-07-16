package com.myvocab.myvocab.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.domain.search.GetSearchWordSetsUseCase
import com.myvocab.myvocab.util.Resource
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel
@Inject
constructor(
        private val getSearchWordSetsUseCase: GetSearchWordSetsUseCase,
        context: Application
) : AndroidViewModel(context) {

    val wordSets: MutableLiveData<Resource<List<WordSetUseCaseResult>>> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()

    init {
        loadWordSets()
    }

    fun loadWordSets() {
        val wordSetsDisposable = getSearchWordSetsUseCase
                .getWordSets()
                .toObservable()
                .publish {
                    it.timeout(500, TimeUnit.MILLISECONDS, Observable.create<MutableList<WordSetUseCaseResult>> {
                        wordSets.postValue(Resource.Loading())
                    })
                            .onErrorResumeNext(Observable.empty())
                            .mergeWith(it)
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    wordSets.postValue(Resource.Success(it))
                }, {
                    wordSets.postValue(Resource.Error(it))
                })

        compositeDisposable.clear()
        compositeDisposable.add(wordSetsDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}