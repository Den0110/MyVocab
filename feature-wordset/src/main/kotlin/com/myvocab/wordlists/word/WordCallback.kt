package com.myvocab.wordlists.word

import com.myvocab.domain.entities.Word

open class WordCallback {
    open fun onClick(word: Word, savedLocally: Boolean) {}
    open fun onNeedToLearnChanged(word: Word, state: Boolean) {}
}