package com.myvocab.wordlists.wordset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.usecases.wordset.GetWordSetOptionsUseCaseResult
import com.myvocab.domain.usecases.wordset.LoadWordSetUseCaseResultDiffCallback
import com.myvocab.wordlists.R
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