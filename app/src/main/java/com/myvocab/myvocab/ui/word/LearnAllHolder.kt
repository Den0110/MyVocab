package com.myvocab.myvocab.ui.word

import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R

class LearnAllHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val needToLearnCheckBox: CheckBox = view.findViewById(R.id.need_to_learn_checkbox)

    fun bind(state: Boolean, callback: LearnAllCallback? = null) {
        needToLearnCheckBox.isChecked = state
        if(callback != null){
            needToLearnCheckBox.setOnClickListener {
                callback.onNeedToLearnAll(needToLearnCheckBox.isChecked)
            }
        }
    }

}