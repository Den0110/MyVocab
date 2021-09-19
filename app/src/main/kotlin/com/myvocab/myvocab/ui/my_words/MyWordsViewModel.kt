package com.myvocab.myvocab.ui.my_words

import com.myvocab.myvocab.data.model.WordSetDbModel
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.ui.word.BaseWordListViewModel
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MyWordsViewModel
@Inject
constructor(
        private val wordRepository: WordRepository
) : BaseWordListViewModel(wordRepository) {

    private val compositeDisposable = CompositeDisposable()

    fun loadMyWords() {
        compositeDisposable.add(wordRepository
                .getWordsByWordSetId(WordSetDbModel.MY_WORDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _words.value = Resource.Success(it.reversed().toMutableList())
                }, {
                    _words.value = Resource.Error(it)
                })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}