package com.myvocab.myvocab.ui.word_set

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.GetWordSetOptionsUseCaseResult
import com.myvocab.myvocab.data.model.LoadWordSetUseCaseResultDiffCallback
import com.myvocab.myvocab.data.model.WordSet
import javax.inject.Inject

class WordSetListAdapter
@Inject
constructor(
        wordSetResultCallback: LoadWordSetUseCaseResultDiffCallback
) : ListAdapter<GetWordSetOptionsUseCaseResult, WordSetHolder>(wordSetResultCallback) {

    var onClick: ((WordSet) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            WordSetHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_word_set, parent, false))

    override fun onBindViewHolder(holder: WordSetHolder, position: Int) =
            holder.bind(getItem(position), onClick)

}