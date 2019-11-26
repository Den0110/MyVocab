package com.myvocab.myvocab.ui.vocab

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.myvocab.myvocab.ui.vocab.learned_words.LearnedWordsFragment
import com.myvocab.myvocab.di.vocab.in_learning_words.InLearningWordsFragment
import com.myvocab.myvocab.R

class VocabPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        const val PAGE_COUNT = 2
    }

    override fun getItem(position: Int): Fragment =
            when (position) {
                0 -> InLearningWordsFragment()
                else -> LearnedWordsFragment()
            }

    override fun getPageTitle(position: Int): CharSequence? =
            when (position) {
                0 -> context.getString(R.string.in_learning)
                else -> context.getString(R.string.learned)
            }

    override fun getCount(): Int = PAGE_COUNT
}