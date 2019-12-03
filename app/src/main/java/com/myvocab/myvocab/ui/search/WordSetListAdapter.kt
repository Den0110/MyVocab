package com.myvocab.myvocab.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.data.model.WordSetDiffCallback
import javax.inject.Inject

class WordSetListAdapter
@Inject
constructor(wordSetDiffCallback: WordSetDiffCallback) : ListAdapter<WordSet, WordSetHolder>(wordSetDiffCallback) {

    var addCallback: OnAddWordSet? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordSetHolder
            = WordSetHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.word_set_list_item, parent, false))

    override fun onBindViewHolder(holder: WordSetHolder, position: Int) =
            holder.bind(getItem(position), addCallback)


    interface OnAddWordSet {
        fun onAdd(wordSet: WordSet)
    }

}