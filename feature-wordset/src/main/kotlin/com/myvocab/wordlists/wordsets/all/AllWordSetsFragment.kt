package com.myvocab.wordlists.wordsets.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.myvocab.commonui.MainNavigationFragment
import com.myvocab.domain.common.Resource
import com.myvocab.wordlists.R
import com.myvocab.wordlists.databinding.FragmentAllWordsetsBinding
import com.myvocab.wordlists.wordset.WordSetListAdapter
import timber.log.Timber
import javax.inject.Inject

class AllWordSetsFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentAllWordsetsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: AllWordSetsViewModel

    @Inject
    lateinit var wordSetListAdapter: WordSetListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAllWordsetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(AllWordSetsViewModel::class.java)

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.loadWordSets() }

        binding.wordSetsRecyclerView.adapter = wordSetListAdapter

        wordSetListAdapter.onClick = { wordSet ->
            findNavController().navigate(R.id.to_word_set_details, bundleOf("word_set" to wordSet))
        }

        viewModel.getWordSetsOptions.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                    binding.messageFailedToLoad.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.messageFailedToLoad.visibility = View.GONE
                    if (wordSetListAdapter.itemCount == 0 && it.data.isNotEmpty()) {
                        binding.wordSetsRecyclerView.alpha = 0f
                        binding.wordSetsRecyclerView.animate().alpha(1f).setDuration(400).start()
                    }
                    wordSetListAdapter.submitList(it.data)
                }
                is Resource.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.messageFailedToLoad.visibility = View.VISIBLE
                    Timber.e(it.error)
                }
            }
        })

    }

}