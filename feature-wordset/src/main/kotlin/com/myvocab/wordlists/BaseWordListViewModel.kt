package com.myvocab.wordlists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myvocab.core.util.Event
import com.myvocab.domain.common.Resource
import com.myvocab.domain.common.withUpdatedList
import com.myvocab.domain.entities.Word
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.wordlists.word.WordCallback
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortType { BY_DEFAULT, ALPHABETICALLY, BY_PROGRESS_LEVEL }

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

        when (sortType) {
            SortType.ALPHABETICALLY -> finalList.sortBy { it.word }
            SortType.BY_PROGRESS_LEVEL -> finalList.sortBy { it.knowingLevel }
            else -> {
            }
        }

        words.withNewData(finalList)
    }

    val showWordDialogEvent: MutableLiveData<Event<Pair<Word, Boolean>>> = MutableLiveData()

    private val _addToMyWordsResultEvent = MutableSharedFlow<Pair<Word, Boolean>>()
    val addToMyWordsResultEvent = _addToMyWordsResultEvent.asSharedFlow()

    private val _wordDeleteError = MutableSharedFlow<Unit>()
    val wordDeleteError = _wordDeleteError.asSharedFlow()

    val wordCallback = object : WordCallback() {

        override fun onClick(word: Word, savedLocally: Boolean) {
            showWordDialogEvent.value = Event(Pair(word, savedLocally))
        }

        override fun onNeedToLearnChanged(word: Word, state: Boolean) {
            updateWord(word) { needToLearn = state }
        }

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
        updateWord(word) { knowingLevel = 3 }
    }

    fun resetProgress(word: Word) {
        updateWord(word) { knowingLevel = 0 }
    }

    fun addToMyWords(word: Word) {
        viewModelScope.launch {
            val w = word.copy(id = null)
            val result = try {
                wordRepository.addMyWord(w)
                true
            } catch (e: Exception) {
                false
            }
            _addToMyWordsResultEvent.emit(Pair(w, result))
        }
    }

    fun delete(word: Word) {
        viewModelScope.launch {
            try {
                wordRepository.deleteWord(word)
                _words.emit(_words.value.withUpdatedList { remove(word) })
            } catch (e: Exception) {
                _wordDeleteError.emit(Unit)
            }
        }
    }

    fun applyNeedToLearnState(words: List<Word>, state: Boolean) {
        words.forEach {
            it.needToLearn = state
        }
        viewModelScope.launch {
            try {
                wordRepository.updateWords(words)
            } catch (e: Exception) {
            }
        }
    }

    private fun updateWord(word: Word, block: Word.() -> Unit) {
        val wordId = word.id
        val list = _words.value.data ?: mutableListOf()
        val index = list.indexOfFirst { w -> w.id == wordId }
        if (index > -1) {
            val newWord = list[index].copy().apply { block(this) }
            applyUpdate(newWord) {
                _words.value = _words.value.withUpdatedList { set(index, newWord) }
            }
        }
    }

    private fun applyUpdate(word: Word, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                wordRepository.updateWord(word)
                onSuccess?.invoke()
            } catch (e: Exception) {
            }
        }
    }

}