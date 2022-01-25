package com.myvocab.wordlists.wordsets.in_learning_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.commonui.MainNavigationFragment
import com.myvocab.domain.common.Resource
import com.myvocab.wordlists.R
import com.myvocab.wordlists.databinding.FragmentInLearningWordsBinding
import com.myvocab.wordlists.wordset.WordSetListAdapter
import com.myvocab.wordlists.wordsets.WordSetsFragmentDirections
import javax.inject.Inject

class InLearningWordSetsFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentInLearningWordsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: InLearningWordSetsViewModel

    @Inject
    lateinit var wordSetListAdapter: WordSetListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_in_learning_words, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(InLearningWordSetsViewModel::class.java)

        binding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.loadInLearningWords() }

        binding.recyclerView.adapter = wordSetListAdapter

        wordSetListAdapter.onClick = { wordSet ->
            findNavController().navigate(WordSetsFragmentDirections.toWordSetDetails(wordSet))
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