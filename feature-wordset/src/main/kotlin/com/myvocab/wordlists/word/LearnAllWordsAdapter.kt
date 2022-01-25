package com.myvocab.wordlists.word

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.domain.entities.Word
import com.myvocab.wordlists.R
import javax.inject.Inject

class LearnAllWordsAdapter
@Inject
constructor() : RecyclerView.Adapter<LearnAllHolder>() {

    var needToLearnAll: Boolean = false
    var learnAllCallback: LearnAllCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnAllHolder {
        return LearnAllHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_vocab_learn_all, parent, false))
    }

    override fun onBindViewHolder(holder: LearnAllHolder, position: Int) {
        holder.bind(needToLearnAll, learnAllCallback)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_vocab_learn_all
    }

    override fun getItemCount(): Int {
        return 1
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