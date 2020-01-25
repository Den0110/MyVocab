package com.myvocab.myvocab.ui.word

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import java.util.*

class WordHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        private const val ALPHA_ANIMATION_DURATION = 200L
    }

    private val knowingLevel: ImageView = view.findViewById(R.id.knowing_level)
    private val wordView: TextView = view.findViewById(R.id.word_view)
    private val translateView: TextView = view.findViewById(R.id.translate_view)
    private val needToLearnCheckBox: CheckBox = view.findViewById(R.id.need_to_learn_checkbox)

    fun bind(word: Word, callback: WordCallback? = null, savedLocally: Boolean? = false) {
        wordView.text = word.word?.toLowerCase(Locale.getDefault())
        translateView.text = word.translation?.toLowerCase(Locale.getDefault())

        if(savedLocally == true) {
            needToLearnCheckBox.visibility = View.VISIBLE
            needToLearnCheckBox.isChecked = word.needToLearn
            updateLearningState(word.needToLearn, false)

            knowingLevel.visibility = View.VISIBLE
            knowingLevel.setImageDrawable(when {
                word.knowingLevel == 0 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_empty_18dp)
                word.knowingLevel == 1 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_red_18dp)
                word.knowingLevel == 2 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_yellow_18dp)
                word.knowingLevel > 2 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_green_18dp)
                else -> null
            })
        } else {
            needToLearnCheckBox.visibility = View.GONE
            knowingLevel.visibility = View.GONE
            updateLearningState(state = true, animate = false)
        }

        if (callback != null) {
            view.setOnClickListener {
                callback.onClick(word, savedLocally == true)
            }

            needToLearnCheckBox.setOnClickListener {
                callback.onNeedToLearnChanged(word, needToLearnCheckBox.isChecked)
                updateLearningState(needToLearnCheckBox.isChecked, true)
            }
        }
    }

    private fun updateLearningState(state: Boolean, animate: Boolean){
        val alpha = if(state) 1f else .5f
        if(animate){
            wordView.animate().alpha(alpha).duration = ALPHA_ANIMATION_DURATION
            translateView.animate().alpha(alpha).duration = ALPHA_ANIMATION_DURATION
            knowingLevel.animate().alpha(alpha).duration = ALPHA_ANIMATION_DURATION
        } else {
            wordView.alpha = alpha
            translateView.alpha = alpha
            knowingLevel.alpha = alpha
        }
    }

}