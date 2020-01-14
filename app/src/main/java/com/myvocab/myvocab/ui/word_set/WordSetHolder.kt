package com.myvocab.myvocab.ui.word_set

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSetUseCaseResult

class WordSetHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private var title: TextView = view.findViewById(R.id.title)
    private var wordCount: TextView = view.findViewById(R.id.word_count)
    private var learningPercentage: TextView = view.findViewById(R.id.learning_percentage)
    private var knowingLevel: ImageView = view.findViewById(R.id.knowing_level)

    fun bind(wordSetResult: WordSetUseCaseResult, callbackClickListener: WordSetListAdapter.OnWordSetClickListener?) {
        title.text = wordSetResult.wordSet.title
        wordCount.text = view.context.resources.getQuantityString(
                R.plurals.word_count,
                wordSetResult.wordSet.words.size,
                wordSetResult.wordSet.words.size
        )

        if (wordSetResult.savedLocally && wordSetResult.learningPercentage != null) {
            val percentage = wordSetResult.learningPercentage!!
            learningPercentage.visibility = View.VISIBLE
            learningPercentage.text = "$percentage%"
            knowingLevel.visibility = View.VISIBLE
            knowingLevel.setImageDrawable(when(percentage){
                in 0..24 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_empty_18dp)
                in 25..49 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_red_18dp)
                in 50..74 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_yellow_18dp)
                in 75..100 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_green_18dp)
                else -> null
            })
        }

        view.setOnClickListener { callbackClickListener?.onClick(wordSetResult.wordSet) }
    }

}