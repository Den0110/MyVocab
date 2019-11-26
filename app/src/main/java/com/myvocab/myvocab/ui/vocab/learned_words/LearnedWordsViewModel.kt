package com.myvocab.myvocab.ui.vocab.learned_words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class LearnedWordsViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        context: Application
) : AndroidViewModel(context) {

    companion object {
        private val TAG = "LearnedWordsViewModel"
    }

    var words: MutableLiveData<Resource<List<Word>>> = MutableLiveData()
    var deleteWordError: MutableLiveData<Throwable> = MutableLiveData()

    private var getWordsDisposable: Disposable? = null
    private var compositeDisposable = CompositeDisposable()

    init {
        loadLearnedWords()
    }

    fun loadLearnedWords(){
        getWordsDisposable?.dispose()
        getWordsDisposable = wordRepository.getLearnedWords()
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribe({
                    words.postValue(Resource.success(it))
                },{
                    words.postValue(Resource.error(it))
                })
    }

    fun deleteWord(word: Word){
        val deleteWordDisposable = wordRepository.deleteWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    words.postValue(Resource.success(words.value?.data?.toMutableList()?.apply { remove(word) }))
                }, {
                    deleteWordError.postValue(it)
                })
        compositeDisposable.add(deleteWordDisposable)
    }

    override fun onCleared() {
        getWordsDisposable?.dispose()
        compositeDisposable.clear()
    }

}