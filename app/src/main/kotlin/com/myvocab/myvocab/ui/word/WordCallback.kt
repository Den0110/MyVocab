package com.myvocab.myvocab.ui.word

import com.myvocab.myvocab.data.model.Word

open class WordCallback {
    open fun onClick(word: Word, savedLocally: Boolean) {}
    open fun onNeedToLearnChanged(word: Word, state: Boolean) {}
}