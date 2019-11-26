package com.myvocab.myvocab.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import javax.inject.Inject

class SettingsViewModel
@Inject
constructor(
        private val wordRepository: WordRepository,
        context: Application
) : AndroidViewModel(context) {

    companion object {
        private val TAG = "SettingsViewModel"
    }

    fun removeAllWords() {
        wordRepository.deleteAllWords().subscribe()
    }

}