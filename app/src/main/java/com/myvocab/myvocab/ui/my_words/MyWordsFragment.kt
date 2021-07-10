package com.myvocab.myvocab.ui.my_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.databinding.FragmentMyWordsBinding
import com.myvocab.myvocab.ui.word.BaseWordListFragment
import com.myvocab.myvocab.util.Resource
import com.myvocab.myvocab.util.findNavController
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
                              savedInstanceState: Bundle?): View {
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

        recycler_view.adapter = adapter

        viewModel.words.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                }
                Resource.Status.SUCCESS -> {
                    swipe_refresh_layout.isRefreshing = false
                    if (it.data.isNullOrEmpty()) {
                        message_empty_vocab.visibility = View.VISIBLE
                        adapter.removeAdapter(learnAllWordsAdapter)
                    } else {
                        message_empty_vocab.visibility = View.GONE
                        adapter.addAdapter(0, learnAllWordsAdapter)
                    }
                    wordListAdapter.submitList(it.data)
                    learnAllWordsAdapter.checkIfAllNeedToLearn(it.data)
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

    override fun getContextMenuItems(word: Word, isSavedLocally: Boolean): Array<String> {
        val items = arrayListOf<String>()
        if (!isSavedLocally) {
            items.add(WORD_MENU_ITEMS[2]) // add to my words
        } else {
            if (word.knowingLevel < 3)
                items.add(WORD_MENU_ITEMS[0]) // mark as learned

            if (word.knowingLevel > 0)
                items.add(WORD_MENU_ITEMS[1]) // reset the progress

            items.add(WORD_MENU_ITEMS[3]) // edit
            items.add(WORD_MENU_ITEMS[4]) // delete
        }
        return items.toTypedArray()
    }

    override fun onContextMenuItemClicked(item: String, word: Word) {
        when (item) {
            WORD_MENU_ITEMS[0] -> viewModel.markAsLearned(word)
            WORD_MENU_ITEMS[1] -> viewModel.resetProgress(word)
            WORD_MENU_ITEMS[2] -> viewModel.addToMyWords(word)
            WORD_MENU_ITEMS[3] -> {
                val action = MyWordsFragmentDirections
                        .toAddNewWord().setWordToEdit(word)
                findNavController().navigate(action)
            }
            WORD_MENU_ITEMS[4] -> viewModel.delete(word)
        }
    }

}