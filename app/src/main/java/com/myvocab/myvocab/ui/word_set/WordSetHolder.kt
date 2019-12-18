package com.myvocab.myvocab.ui.word_set

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet

class WordSetHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private var title: TextView = view.findViewById(R.id.title)

    fun bind(wordSet: WordSet, callbackClickListener: WordSetListAdapter.OnWordSetClickListener?){
        title.text = wordSet.title
        view.setOnClickListener { callbackClickListener?.onClick(wordSet) }
    }

}