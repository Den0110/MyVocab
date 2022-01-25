package com.myvocab.wordlists.wordsets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.myvocab.commonui.MainNavigationFragment
import com.myvocab.wordlists.R
import com.myvocab.wordlists.databinding.FragmentWordSetsBinding

class WordSetsFragment: MainNavigationFragment() {

    private lateinit var binding: FragmentWordSetsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentWordSetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = WordSetsPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = requireContext().getString(when(pos) {
                0 -> R.string.all
                1 -> R.string.in_learning
                2 -> R.string.learned
                else -> throw IllegalArgumentException("Wrong page position")
            })
        }.attach()
    }

}