package com.myvocab.myvocab.ui.my_word_sets.learned_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.databinding.FragmentLearnedWordsBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.ui.word_set.WordSetListAdapter
import com.myvocab.myvocab.util.Resource
import com.myvocab.myvocab.util.findNavController
import kotlinx.android.synthetic.main.fragment_in_learning_words.*
import javax.inject.Inject

class LearnedWordSetsFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentLearnedWordsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: LearnedWordSetsViewModel

    @Inject
    lateinit var wordSetListAdapter: WordSetListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_learned_words, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(LearnedWordSetsViewModel::class.java)

        swipe_refresh_layout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        swipe_refresh_layout.setOnRefreshListener { viewModel.loadLearnedWords() }

        recycler_view.adapter = wordSetListAdapter

        wordSetListAdapter.onClickListenerClickListener = object : WordSetListAdapter.OnWordSetClickListener {
            override fun onClick(wordSet: WordSet) {
                findNavController().navigate(R.id.to_word_set_details, bundleOf("word_set" to wordSet))
            }
        }

        wordSetListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (recycler_view.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })

        viewModel.wordSets.observe(viewLifecycleOwner, { words ->
            when (words.status) {
                Resource.Status.LOADING -> swipe_refresh_layout.isRefreshing = true
                Resource.Status.SUCCESS -> {
                    wordSetListAdapter.submitList(words.data)
                    swipe_refresh_layout.isRefreshing = false
                    if(words.data.isNullOrEmpty()){
                        message_empty_vocab.visibility = View.VISIBLE
                    } else {
                        message_empty_vocab.visibility = View.GONE
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(context, words.error?.toString(), Toast.LENGTH_SHORT).show()
                    swipe_refresh_layout.isRefreshing = false
                }
            }
        })

    }

}
