package com.myvocab.myvocab.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.domain.search.GetSearchWordSetsUseCase
import com.myvocab.myvocab.util.Resource
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
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
                        wordSets.postValue(Resource.loading())
                    })
                            .onErrorResumeNext(Observable.empty())
                            .mergeWith(it)
                }
                .subscribe({
                    wordSets.postValue(Resource.success(it))
                }, {
                    wordSets.postValue(Resource.error(it))
                })
        compositeDisposable.clear()
        compositeDisposable.add(wordSetsDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}