package com.myvocab.myvocab.ui.vocab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.model.WordDiffCallback
import javax.inject.Inject

class WordListAdapter
@Inject
constructor(wordDiffCallback: WordDiffCallback) : ListAdapter<Word, WordHolder>(wordDiffCallback) {

    var removeCallback: OnRemoveWordCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder
            = WordHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.vocab_list_item, parent, false))

    override fun onBindViewHolder(holder: WordHolder, position: Int) =
        holder.bind(getItem(position), removeCallback)

    interface OnRemoveWordCallback {
        fun onRemove(word: Word)
    }

}