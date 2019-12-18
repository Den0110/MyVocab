package com.myvocab.myvocab.ui.my_word_sets.in_learning_words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class InLearningWordSetsViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        context: Application
) : AndroidViewModel(context) {

    companion object {
        private const val TAG = "InLearningWordSetsVM"
    }

    var wordSets: MutableLiveData<Resource<List<WordSet>>> = MutableLiveData()

    private var getWordsDisposable: Disposable? = null
    private var compositeDisposable = CompositeDisposable()

    init {
        loadInLearningWords()
    }

    fun loadInLearningWords(){
        getWordsDisposable?.dispose()
        getWordsDisposable = wordRepository
                .getInLearningWordSets()
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe({
                    wordSets.postValue(Resource.success(it))
                },{
                    wordSets.postValue(Resource.error(it))
                })
    }

    override fun onCleared() {
        getWordsDisposable?.dispose()
        compositeDisposable.clear()
    }

}