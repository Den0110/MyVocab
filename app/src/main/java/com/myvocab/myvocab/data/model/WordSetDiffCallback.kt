package com.myvocab.myvocab.data.model

import androidx.recyclerview.widget.DiffUtil

class WordSetDiffCallback : DiffUtil.ItemCallback<WordSet>() {

    override fun areItemsTheSame(oldItem: WordSet, newItem: WordSet): Boolean = newItem.id == oldItem.id

    override fun areContentsTheSame(oldItem: WordSet, newItem: WordSet): Boolean = newItem == oldItem

}