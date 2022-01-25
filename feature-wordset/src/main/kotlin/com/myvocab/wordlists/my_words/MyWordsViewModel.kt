package com.myvocab.wordlists.my_words

import androidx.lifecycle.viewModelScope
import com.myvocab.domain.common.Resource
import com.myvocab.domain.repositories.WordRepository
import com.myvocab.wordlists.BaseWordListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyWordsViewModel
@Inject
constructor(
    private val wordRepository: WordRepository
) : BaseWordListViewModel(wordRepository) {

    fun loadMyWords() {
        viewModelScope.launch {
            try {
                val words = wordRepository.getWordsByWordSetId("my_words") // todo refactor
                _words.emit(Resource.Success(words.reversed().toMutableList()))
            } catch (e: Exception) {
                _words.emit(Resource.Error(e))
            }
        }
    }

}