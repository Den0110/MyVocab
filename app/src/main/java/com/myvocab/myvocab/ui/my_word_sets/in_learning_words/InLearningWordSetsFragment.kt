package com.myvocab.myvocab.ui.my_word_sets.in_learning_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.databinding.FragmentInLearningWordsBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.ui.word_set.WordSetListAdapter
import com.myvocab.myvocab.util.*
import kotlinx.android.synthetic.main.fragment_in_learning_words.*
import javax.inject.Inject

class InLearningWordSetsFragment : MainNavigationFragment() {

    companion object {
        private const val TAG = "InLearningWordsFragment"
    }

    private lateinit var binding: FragmentInLearningWordsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: InLearningWordSetsViewModel

    @Inject
    lateinit var wordSetListAdapter: WordSetListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_in_learning_words, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(InLearningWordSetsViewModel::class.java)

        swipe_refresh_layout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
        swipe_refresh_layout.setOnRefreshListener { viewModel.loadInLearningWords() }

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

        viewModel.wordSets.observe(viewLifecycleOwner, Observer { words ->
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