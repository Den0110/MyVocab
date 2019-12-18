package com.myvocab.myvocab.ui.settings

import androidx.lifecycle.ViewModel
import com.myvocab.myvocab.data.source.WordRepository
import javax.inject.Inject

class SettingsViewModel
@Inject
constructor(
        private val wordRepository: WordRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    fun removeAllWords() {
        wordRepository.deleteAllWords().subscribe()
        wordRepository.deleteAllWordSets().subscribe()
    }

}