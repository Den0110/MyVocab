package com.myvocab.myvocab.di.vocab.in_learning_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.databinding.FragmentInLearningWordsBinding
import com.myvocab.myvocab.ui.BaseFragment
import com.myvocab.myvocab.ui.vocab.WordListAdapter
import com.myvocab.myvocab.ui.vocab.in_learning_words.InLearningWordsViewModel
import com.myvocab.myvocab.util.*
import kotlinx.android.synthetic.main.fragment_in_learning_words.*
import javax.inject.Inject

class InLearningWordsFragment : BaseFragment() {

    companion object {
        const val TAG = "InLearningWordsFragment"
    }

    private lateinit var binding: FragmentInLearningWordsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: InLearningWordsViewModel

    @Inject
    lateinit var wordListAdapter: WordListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_in_learning_words, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, viewModelFactory).get(InLearningWordsViewModel::class.java)

        swipe_refresh_layout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
        swipe_refresh_layout.setOnRefreshListener { viewModel.loadInLearningWords() }

        recycler_view.adapter = wordListAdapter

        wordListAdapter.removeCallback = object : WordListAdapter.OnRemoveWordCallback {
            override fun onRemove(word: Word) {
                viewModel.deleteWord(word)
            }
        }

        wordListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (recycler_view.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })

        viewModel.words.observe(viewLifecycleOwner, Observer { words ->
            when (words.status) {
                Resource.Status.LOADING -> swipe_refresh_layout.isRefreshing = true
                Resource.Status.SUCCESS -> {
                    wordListAdapter.submitList(words.data)
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