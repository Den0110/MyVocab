package com.myvocab.myvocab.data.model

import androidx.recyclerview.widget.DiffUtil

class WordDiffCallback : DiffUtil.ItemCallback<Word>() {

    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean = newItem.id == oldItem.id

    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean = newItem == oldItem

}