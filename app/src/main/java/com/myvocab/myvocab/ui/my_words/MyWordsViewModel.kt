package com.myvocab.myvocab.ui.my_words

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.ui.word.WordCallback
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MyWordsViewModel
@Inject
constructor(
        private val wordRepository: WordRepository
) : ViewModel() {

    val words: MutableLiveData<Resource<List<Word>>> = MutableLiveData()
    var deleteWordError: MutableLiveData<Throwable> = MutableLiveData()

    private val compositeDisposable = CompositeDisposable()
    private val updateWordDisposable = CompositeDisposable()

    val wordCallback = object : WordCallback() {
        override fun onNeedToLearnChanged(word: Word, state: Boolean) {
            if(word.needToLearn != state) {
                words.value = (Resource.success(words.value?.data?.toMutableList()?.apply {
                    first { w -> w.id == word.id }.apply { needToLearn = state }
                    updateWord(word)
                }))
            }
        }
    }

    fun loadMyWords() {
        compositeDisposable.add(wordRepository
                .getWordSetFromDb("my_words")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    words.value = Resource.success(it.words)
                }, {
                    words.value = Resource.error(it)
                })
        )
    }

    fun updateWord(word: Word){
        updateWordDisposable.clear()
        updateWordDisposable.add(wordRepository
                .updateWord(word)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun deleteWord(word: Word) {
        compositeDisposable.add(wordRepository
                .deleteWord(word)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    words.value = Resource.success(words.value?.data?.toMutableList()?.apply { remove(word) })
                }, {
                    deleteWordError.value = it
                })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}