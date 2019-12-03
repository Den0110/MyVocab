package com.myvocab.myvocab.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.WordSet
import com.myvocab.myvocab.databinding.FragmentSearchBinding
import com.myvocab.myvocab.ui.add_new_word.AddNewWordActivity
import com.myvocab.myvocab.util.Resource
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class SearchFragment : DaggerFragment() {

    private lateinit var binding: FragmentSearchBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchViewModel

    @Inject
    lateinit var wordSetListAdapter: WordSetListAdapter

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

        swipe_refresh_layout.setOnRefreshListener { viewModel.loadWordSets() }

        word_sets_recycler_view.adapter = wordSetListAdapter

        wordSetListAdapter.addCallback = object : WordSetListAdapter.OnAddWordSet {
            override fun onAdd(wordSet: WordSet) {
                Log.d(AddNewWordActivity.TAG, "Add \"${wordSet.title}\" word set")
                compositeDisposable.clear()
                compositeDisposable.add(
                        viewModel.addWords(wordSet.words).subscribe({
                            Snackbar.make(view, "${wordSet.words.size} words from ${wordSet.title} was added", Snackbar.LENGTH_SHORT).show()
                            Navigation.findNavController(view).navigate(R.id.vocabFragment)
                        }, { e ->
                            Log.e(AddNewWordActivity.TAG, e.message)
                            Snackbar.make(view, "Error, words haven't added", Snackbar.LENGTH_SHORT).show()
                        })
                )
            }
        }

        viewModel.wordSets.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Resource.Status.LOADING -> swipe_refresh_layout.isRefreshing = true
                Resource.Status.SUCCESS -> {
                    swipe_refresh_layout.isRefreshing = false
                    wordSetListAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    swipe_refresh_layout.isRefreshing = false
                    Log.e(AddNewWordActivity.TAG, it.error?.message)
                }
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}