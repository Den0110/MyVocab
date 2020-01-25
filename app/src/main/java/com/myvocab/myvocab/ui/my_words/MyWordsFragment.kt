package com.myvocab.myvocab.ui.my_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentMyWordsBinding
import com.myvocab.myvocab.ui.word.BaseWordListFragment
import com.myvocab.myvocab.util.Resource
import kotlinx.android.synthetic.main.fragment_in_learning_words.recycler_view
import kotlinx.android.synthetic.main.fragment_in_learning_words.swipe_refresh_layout
import kotlinx.android.synthetic.main.fragment_my_words.*
import timber.log.Timber
import javax.inject.Inject

class MyWordsFragment : BaseWordListFragment() {

    private lateinit var binding: FragmentMyWordsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    override val viewModel: MyWordsViewModel
            by lazy { ViewModelProvider(this, viewModelFactory).get(MyWordsViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_words, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh_layout.setOnRefreshListener { viewModel.loadMyWords() }

        wordListAdapter.isSavedLocally = true
        wordListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (recycler_view.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })

        recycler_view.adapter = wordListAdapter

        viewModel.words.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                }
                Resource.Status.SUCCESS -> {
                    swipe_refresh_layout.isRefreshing = false
                    if (it.data.isNullOrEmpty()) {
                        message_empty_vocab.visibility = View.VISIBLE
                    } else {
                        message_empty_vocab.visibility = View.GONE
                    }
                    recycler_view.post { wordListAdapter.checkIfAllNeedToLearn(it.data) }
                    wordListAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    swipe_refresh_layout.isRefreshing = false
                    Toast.makeText(context, it.error?.message, Toast.LENGTH_SHORT).show()
                    Timber.e(it.error)
                }
            }
        })

        add_new_word_btn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_add_new_word)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMyWords()
    }

}