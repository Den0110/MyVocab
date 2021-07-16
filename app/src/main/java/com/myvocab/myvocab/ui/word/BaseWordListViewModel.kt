package com.myvocab.myvocab.ui.word

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.util.Event
import com.myvocab.myvocab.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

enum class SortType { BY_DEFAULT, ALPHABETICALLY, BY_KNOWLEDGE_LEVEL }

open class BaseWordListViewModel(
    private val wordRepository: WordRepository
) : ViewModel() {

    protected val _words = MutableStateFlow<Resource<MutableList<Word>?>>(Resource.Loading())
    val words = _words.asStateFlow()

    private val _searchFilter = MutableStateFlow("")
    val searchFilter = _searchFilter.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.BY_DEFAULT)
    val sortType = _sortType.asStateFlow()

    val filteredWords = combine(_words, _searchFilter.debounce(300), _sortType) { words, filter, sortType ->
        val finalList = words.data
            ?.filter { it.word.contains(filter) }
            ?.toMutableList() ?: mutableListOf()

        when(sortType) {
            SortType.ALPHABETICALLY -> finalList.sortBy { it.word }
            SortType.BY_KNOWLEDGE_LEVEL -> finalList.sortBy { it.knowingLevel }
            else -> {}
        }

        words.withNewData(finalList)
    }

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
            _words.value = _words.value.withNewData(_words.value.data?.toMutableList()?.apply {
                val w = first { w -> w.id == word.id }.apply { needToLearn = state }
                update(w)
            })
        }

    }

    fun update(word: Word) {
        wordOperationsDisposable.add(
            wordRepository
                .updateWord(word)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun onSearchFilterChanged(text: String) {
        viewModelScope.launch {
            _searchFilter.emit(text)
        }
    }

    fun onSortTypeChanged(sortType: SortType) {
        viewModelScope.launch {
            _sortType.emit(sortType)
        }
    }

    fun markAsLearned(word: Word) {
        _words.value.data?.let {
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
        _words.value.data?.let {
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
        wordOperationsDisposable.add(
            wordRepository
                .addMyWord(w)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    addToMyWordsResultEvent.value = Event(Pair(w, true))
                }, {
                    addToMyWordsResultEvent.value = Event(Pair(w, false))
                })
        )
    }

    fun delete(word: Word) {
        wordOperationsDisposable.add(
            wordRepository
                .deleteWord(word)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _words.value.data?.let {
                        val i = it.indexOfFirst { w -> w.id == word.id }
                        it.remove(word)

                        if (it.size == 0) {
                            _words.value = Resource.Success(mutableListOf())
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