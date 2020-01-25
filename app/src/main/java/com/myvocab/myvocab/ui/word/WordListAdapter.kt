package com.myvocab.myvocab.ui.word

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.model.WordDiffCallback
import javax.inject.Inject

class WordListAdapter
@Inject
constructor(wordDiffCallback: WordDiffCallback) : ListAdapter<Word, RecyclerView.ViewHolder>(wordDiffCallback) {

    companion object {
        private const val LEARN_ALL_ITEM_VIEW_TYPE = 1
        private const val COMMON_ITEM_VIEW_TYPE = 2
    }

    var callback: WordCallback? = null
    var needToLearnAll: Boolean = false
    var isSavedLocally: Boolean? = false
    var learnAllCallback: LearnAllCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LEARN_ALL_ITEM_VIEW_TYPE) {
            LearnAllHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.vocab_learn_all_list_item, parent, false))
        } else {
            WordHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.vocab_list_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isSavedLocally == true) {
            if (position == 0) {
                (holder as LearnAllHolder).bind(needToLearnAll, learnAllCallback)
            } else {
                val word = getItem(position - 1)
                (holder as WordHolder).bind(word, callback, isSavedLocally)
            }
        } else {
            (holder as WordHolder).bind(getItem(position), callback, isSavedLocally)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSavedLocally == true && position == 0) {
            LEARN_ALL_ITEM_VIEW_TYPE
        } else {
            COMMON_ITEM_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return if (isSavedLocally == true && super.getItemCount() > 0) {
            super.getItemCount() + 1
        } else {
            super.getItemCount()
        }
    }

    fun checkIfAllNeedToLearn(words: List<Word>?) {
        var allNeedToLearn = true

        if (words.isNullOrEmpty()) {
            allNeedToLearn = false
        } else {
            for (word in words) {
                if (!word.needToLearn) {
                    allNeedToLearn = false
                    break
                }
            }
        }

        if (needToLearnAll != allNeedToLearn) {
            needToLearnAll = allNeedToLearn
            notifyItemChanged(0)
        }
    }

}