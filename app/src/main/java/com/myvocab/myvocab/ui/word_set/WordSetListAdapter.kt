package com.myvocab.myvocab.ui.word_set

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSetUseCaseResult
import com.myvocab.myvocab.data.model.LoadWordSetUseCaseResultDiffCallback
import com.myvocab.myvocab.data.model.WordSet
import javax.inject.Inject

class WordSetListAdapter
@Inject
constructor(
        wordSetResultCallback: LoadWordSetUseCaseResultDiffCallback
) : ListAdapter<WordSetUseCaseResult, WordSetHolder>(wordSetResultCallback) {

    var onClickListenerClickListener: OnWordSetClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            WordSetHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.word_set_list_item, parent, false))

    override fun onBindViewHolder(holder: WordSetHolder, position: Int) =
            holder.bind(getItem(position), onClickListenerClickListener)

    interface OnWordSetClickListener {
        fun onClick(wordSet: WordSet)
    }

}