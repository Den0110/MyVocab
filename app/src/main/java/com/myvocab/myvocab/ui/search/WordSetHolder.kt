package com.myvocab.myvocab.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet

class WordSetHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var title: TextView = view.findViewById(R.id.title)
    private var addWordSetBtn: ImageView = view.findViewById(R.id.add_word_set)

    fun bind(wordSet: WordSet, addCallback: WordSetListAdapter.OnAddWordSet?){
        title.text = wordSet.title
        addWordSetBtn.setOnClickListener { addCallback?.onAdd(wordSet) }
    }

}