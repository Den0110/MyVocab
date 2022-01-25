package com.myvocab.wordlists.word

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.myvocab.domain.entities.Word
import com.myvocab.domain.entities.WordDiffCallback
import com.myvocab.wordlists.R
import javax.inject.Inject

class WordListAdapter
@Inject
constructor(wordDiffCallback: WordDiffCallback) : ListAdapter<Word, WordHolder>(wordDiffCallback) {

    var callback: WordCallback? = null
    var isSavedLocally: Boolean? = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder {
        return WordHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_word, parent, false))
    }

    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        holder.bind(getItem(position), callback, isSavedLocally)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_word
    }

}