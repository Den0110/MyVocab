package com.myvocab.myvocab.ui.word

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word

class WordHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var knowingLevel: ImageView = view.findViewById(R.id.knowing_level)
    private var wordView: TextView = view.findViewById(R.id.word_view)
    private var translateView: TextView = view.findViewById(R.id.translate_view)

    fun bind(word: Word, removeCallback: WordListAdapter.OnRemoveWordCallback? = null){
        wordView.text = word.word?.toLowerCase()
        translateView.text = word.translation?.toLowerCase()
        knowingLevel.setImageDrawable(when{
            word.knowingLevel == 0 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_empty_18dp)
            word.knowingLevel == 1 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_red_18dp)
            word.knowingLevel == 2 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_yellow_18dp)
            word.knowingLevel > 2 -> ContextCompat.getDrawable(knowingLevel.context, R.drawable.ic_brain_green_18dp)
            else -> null
        })
    }

}