package com.myvocab.wordlists.wordset

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.domain.entities.WordSet
import com.myvocab.domain.usecases.wordset.GetWordSetOptionsUseCaseResult
import com.myvocab.wordlists.R

class WordSetHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private var title: TextView = view.findViewById(R.id.title)
    private var wordCount: TextView = view.findViewById(R.id.word_count)
    private var learningPercentage: TextView = view.findViewById(R.id.learning_percentage)
    private var knowingLevel: ImageView = view.findViewById(R.id.knowing_level)

    fun bind(getWordSetOptionsResult: GetWordSetOptionsUseCaseResult, onClick: ((WordSet) -> Unit)?) {
        title.text = getWordSetOptionsResult.wordSet.title
        wordCount.text = view.context.resources.getQuantityString(
                R.plurals.word_count,
                getWordSetOptionsResult.wordSet.words.size,
                getWordSetOptionsResult.wordSet.words.size
        )

        if (getWordSetOptionsResult.savedLocally && getWordSetOptionsResult.learningPercentage != null) {
            val percentage = getWordSetOptionsResult.learningPercentage!!
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
        } else {
            learningPercentage.visibility = View.GONE
            knowingLevel.visibility = View.GONE
        }

        view.setOnClickListener { onClick?.invoke(getWordSetOptionsResult.wordSet) }
    }

}