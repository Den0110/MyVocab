package com.myvocab.wordlists.wordsets.learned_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.commonui.MainNavigationFragment
import com.myvocab.domain.common.Resource
import com.myvocab.wordlists.R
import com.myvocab.wordlists.databinding.FragmentLearnedWordsBinding
import com.myvocab.wordlists.wordset.WordSetListAdapter
import javax.inject.Inject

class LearnedWordSetsFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentLearnedWordsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: LearnedWordSetsViewModel

    @Inject
    lateinit var wordSetListAdapter: WordSetListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_learned_words, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(LearnedWordSetsViewModel::class.java)

        binding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.loadLearnedWords() }

        binding.recyclerView.adapter = wordSetListAdapter

        wordSetListAdapter.onClick = { wordSet ->
            findNavController().navigate(R.id.to_word_set_details, bundleOf("word_set" to wordSet))
        }

        wordSetListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })

        viewModel.getWordSetsOptions.observe(viewLifecycleOwner, { words ->
            when (words) {
                is Resource.Loading -> binding.swipeRefreshLayout.isRefreshing = true
                is Resource.Success -> {
                    wordSetListAdapter.submitList(words.data)
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (words.data.isNullOrEmpty()) {
                        binding.messageEmptyVocab.visibility = View.VISIBLE
                    } else {
                        binding.messageEmptyVocab.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(context, words.error.toString(), Toast.LENGTH_SHORT).show()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        })

    }

}
