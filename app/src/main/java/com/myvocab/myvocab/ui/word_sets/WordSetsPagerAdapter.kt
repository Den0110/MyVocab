package com.myvocab.myvocab.ui.word_sets

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.myvocab.myvocab.ui.word_sets.all.AllWordSetsFragment
import com.myvocab.myvocab.ui.word_sets.in_learning_words.InLearningWordSetsFragment
import com.myvocab.myvocab.ui.word_sets.learned_words.LearnedWordSetsFragment

class WordSetsPagerAdapter(val fragment: Fragment) : FragmentStateAdapter(fragment) {

    companion object {
        const val PAGE_COUNT = 3
    }

    override fun getItemCount() = PAGE_COUNT

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> AllWordSetsFragment()
            1 -> InLearningWordSetsFragment()
            2 -> LearnedWordSetsFragment()
            else -> throw IllegalArgumentException("Wrong page position")
        }
}