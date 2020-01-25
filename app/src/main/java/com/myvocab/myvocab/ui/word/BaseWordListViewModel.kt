package com.myvocab.myvocab.ui.word

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Event
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

open class BaseWordListViewModel(
        private val wordRepository: WordRepository
) : ViewModel() {

    val words: MediatorLiveData<Resource<MutableList<Word>>> = MediatorLiveData()
    val showWordDialogEvent: MutableLiveData<Event<Pair<Word, Boolean>>> = MutableLiveData()

    val notifyWordChangedEvent: MutableLiveData<Event<Int>> = MutableLiveData()
    val addToMyWordsResultEvent: MutableLiveData<Event<Pair<Word, Boolean>>> = MutableLiveData()
    val notifyWordRemovedEvent: MutableLiveData<Event<Int>> = MutableLiveData()

    private val wordOperationsDisposable = CompositeDisposable()

    val wordCallback = object : WordCallback() {

        override fun onClick(word: Word, savedLocally: Boolean) {
            showWordDialogEvent.value = Event(Pair(word, savedLocally))
        }

        override fun onNeedToLearnChanged(word: Word, state: Boolean) {
            if (word.needToLearn != state) {
                words.value = Resource.success(words.value?.data?.toMutableList()?.apply {
                    val w = first { w -> w.id == word.id }.apply { needToLearn = state }
                    update(w)
                })
            }
        }

    }

    fun update(word: Word) {
        wordOperationsDisposable.add(wordRepository
                .updateWord(word)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun markAsLearned(word: Word) {
        words.value?.data?.let {
            val i = it.indexOfFirst { w -> w.id == word.id }
            it[i].knowingLevel = 3
            wordOperationsDisposable.add(wordRepository
                    .updateWord(it[i])
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        notifyWordChangedEvent.value = Event(i)
                    }
            )
        }
    }

    fun resetProgress(word: Word) {
        words.value?.data?.let {
            val i = it.indexOfFirst { w -> w.id == word.id }
            it[i].knowingLevel = 0
            wordOperationsDisposable.add(wordRepository
                    .updateWord(it[i])
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        notifyWordChangedEvent.value = Event(i)
                    }
            )
        }
    }

    fun addToMyWords(word: Word) {
        val w = word.copy(id = null)
        wordOperationsDisposable.add(wordRepository
                .addMyWord(w)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    addToMyWordsResultEvent.value = Event(Pair(w, true))
                }, {
                    addToMyWordsResultEvent.value = Event(Pair(w, false))
                })
        )
    }

    fun delete(word: Word) {
        wordOperationsDisposable.add(wordRepository
                .deleteWord(word)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    words.value?.data?.let {
                        val i = it.indexOfFirst { w -> w.id == word.id }
                        it.remove(word)

                        if(it.size == 0){
                            words.value = Resource.success(mutableListOf())
                        }
                        notifyWordRemovedEvent.value = Event(i)
                    }
                }, {
                    notifyWordRemovedEvent.value = Event(-1)
                })
        )
    }

    override fun onCleared() {
        wordOperationsDisposable.clear()
    }

}