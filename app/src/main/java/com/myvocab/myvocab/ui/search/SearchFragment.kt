package com.myvocab.myvocab.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.databinding.FragmentSearchBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.ui.word_set.WordSetListAdapter
import com.myvocab.myvocab.util.Resource
import com.myvocab.myvocab.util.findNavController
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber
import javax.inject.Inject

class SearchFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentSearchBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchViewModel

    @Inject
    lateinit var wordSetListAdapter: WordSetListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

        swipe_refresh_layout.setOnRefreshListener { viewModel.loadWordSets() }

        word_sets_recycler_view.adapter = wordSetListAdapter

        wordSetListAdapter.onClickListenerClickListener = object : WordSetListAdapter.OnWordSetClickListener {
            override fun onClick(wordSet: WordSet) {
                findNavController().navigate(R.id.to_word_set_details, bundleOf("word_set" to wordSet))
            }
        }

        viewModel.wordSets.observe(viewLifecycleOwner, {
            when(it.status){
                Resource.Status.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                    message_failed_to_load.visibility = View.GONE
                }
                Resource.Status.SUCCESS -> {
                    swipe_refresh_layout.isRefreshing = false
                    message_failed_to_load.visibility = View.GONE
                    if(wordSetListAdapter.itemCount == 0 && it.data?.size ?: 0 > 0) {
                        word_sets_recycler_view.alpha = 0f
                        word_sets_recycler_view.animate().alpha(1f).setDuration(400).start()
                    }
                    wordSetListAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    swipe_refresh_layout.isRefreshing = false
                    message_failed_to_load.visibility = View.VISIBLE
                    Timber.e(it.error)
                }
            }
        })

    }

}