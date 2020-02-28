package com.myvocab.myvocab.ui.intro

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myvocab.myvocab.R
import kotlinx.android.synthetic.main.fragment_intro_fast_translate.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class IntroFastTranslationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro_fast_translate, container, false)
    }

//    fun showPrompt(){
//        MaterialTapTargetPrompt.Builder(context as Activity)
//                .setTarget(text)
//                .setPrimaryText("Попробуйте Быстрый Перевод")
//                .setSecondaryText("Выделите и скопируйте любое слово из текста")
//                .setBackgroundColour(resources.getColor(R.color.guideBgColor))
//                .setPrimaryTextColour(resources.getColor(R.color.primaryTextColor))
//                .setSecondaryTextColour(resources.getColor(R.color.secondaryTextColor))
//                .setPromptStateChangeListener { prompt, state ->
//                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
//                        // User has pressed the prompt target
//                        prompt.finish()
//                    }
//                }
//                .show()
//    }

}