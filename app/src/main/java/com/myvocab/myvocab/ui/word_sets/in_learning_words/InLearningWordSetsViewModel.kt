package com.myvocab.myvocab.ui.word_sets.in_learning_words

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.domain.my_word_sets.in_learning_words.GetInLearningWordSetsUseCase
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class InLearningWordSetsViewModel
@Inject
constructor(
        private val inLearningWordSetsUseCase: GetInLearningWordSetsUseCase
) : ViewModel() {

    var wordSets: MutableLiveData<Resource<List<WordSetUseCaseResult>>> = MutableLiveData()

    private var getWordsDisposable: Disposable? = null
    private var compositeDisposable = CompositeDisposable()

    init {
        loadInLearningWords()
    }

    fun loadInLearningWords(){
        getWordsDisposable?.dispose()
        getWordsDisposable = inLearningWordSetsUseCase
                .getWordSets()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    wordSets.postValue(Resource.Success(it))
                },{
                    wordSets.postValue(Resource.Error(it))
                })
    }

    override fun onCleared() {
        getWordsDisposable?.dispose()
        compositeDisposable.clear()
    }

}