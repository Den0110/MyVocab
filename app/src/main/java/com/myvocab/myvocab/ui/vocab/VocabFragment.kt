package com.myvocab.myvocab.ui.vocab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentVocabBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.util.findNavController
import kotlinx.android.synthetic.main.fragment_vocab.*

class VocabFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentVocabBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vocab, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        my_words_btn.setOnClickListener {
            findNavController().navigate(R.id.to_my_words)
        }

        my_word_sets_btn.setOnClickListener {
            findNavController().navigate(R.id.to_my_word_sets)
        }

    }

}
