package com.myvocab.myvocab.data.model

import androidx.recyclerview.widget.DiffUtil

data class GetWordSetOptionsUseCaseResult(
        val wordSet: WordSet,
        val savedLocally: Boolean,
        var learningPercentage: Int? = null
)

class LoadWordSetUseCaseResultDiffCallback : DiffUtil.ItemCallback<GetWordSetOptionsUseCaseResult>() {

    override fun areItemsTheSame(oldItem: GetWordSetOptionsUseCaseResult, newItem: GetWordSetOptionsUseCaseResult): Boolean =
            newItem.wordSet.globalId == oldItem.wordSet.globalId

    override fun areContentsTheSame(oldItem: GetWordSetOptionsUseCaseResult, newItem: GetWordSetOptionsUseCaseResult): Boolean =
            newItem == oldItem

}