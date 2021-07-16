package com.myvocab.myvocab.ui.my_word_sets.learned_words

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.domain.my_word_sets.learned_words.GetLearnedWordSetsUseCase
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class LearnedWordSetsViewModel
@Inject
constructor(
        private val learnedWordSetsUseCase: GetLearnedWordSetsUseCase
) : ViewModel() {

    var wordSets: MutableLiveData<Resource<List<WordSetUseCaseResult>>> = MutableLiveData()

    private var getWordsDisposable: Disposable? = null
    private var compositeDisposable = CompositeDisposable()

    init {
        loadLearnedWords()
    }

    fun loadLearnedWords(){
        getWordsDisposable?.dispose()
        getWordsDisposable = learnedWordSetsUseCase
                .getWordSets()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
//                    // there is no sense to show 100% learned status for all word sets
//                    // so, null learningPercentage for all ones
//                    it.forEach { ws -> ws.learningPercentage = null }
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