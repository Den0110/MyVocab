package com.myvocab.myvocab.data.model

import androidx.recyclerview.widget.DiffUtil

data class WordSetUseCaseResult(
        val wordSet: WordSet,
        val savedLocally: Boolean,
        var learningPercentage: Int? = null
)

class LoadWordSetUseCaseResultDiffCallback : DiffUtil.ItemCallback<WordSetUseCaseResult>() {

    override fun areItemsTheSame(oldItem: WordSetUseCaseResult, newItem: WordSetUseCaseResult): Boolean =
            newItem.wordSet.globalId == oldItem.wordSet.globalId

    override fun areContentsTheSame(oldItem: WordSetUseCaseResult, newItem: WordSetUseCaseResult): Boolean =
            newItem == oldItem

}