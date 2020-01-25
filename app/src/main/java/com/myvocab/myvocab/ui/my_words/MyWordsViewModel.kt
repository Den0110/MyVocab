package com.myvocab.myvocab.ui.my_words

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
                .getWordsByWordSetId("my_words")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    words.value = Resource.success(it.reversed().toMutableList())
                }, {
                    words.value = Resource.error(it)
                })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}