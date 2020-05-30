package com.myvocab.myvocab.ui.word

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import javax.inject.Inject

class LearnAllWordsAdapter
@Inject
constructor() : RecyclerView.Adapter<LearnAllHolder>() {

    var needToLearnAll: Boolean = false
    var learnAllCallback: LearnAllCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnAllHolder {
        return LearnAllHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.vocab_learn_all_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: LearnAllHolder, position: Int) {
        holder.bind(needToLearnAll, learnAllCallback)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.vocab_learn_all_list_item
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