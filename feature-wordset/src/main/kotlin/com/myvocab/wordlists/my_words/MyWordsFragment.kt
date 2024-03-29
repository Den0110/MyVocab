package com.myvocab.wordlists.my_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myvocab.domain.common.Resource
import com.myvocab.domain.entities.Word
import com.myvocab.wordlists.BaseWordListFragment
import com.myvocab.wordlists.R
import com.myvocab.wordlists.databinding.FragmentMyWordsBinding
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject

class MyWordsFragment : BaseWordListFragment() {

    private lateinit var binding: FragmentMyWordsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    override val viewModel: MyWordsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { viewModelFactory }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_words, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.loadMyWords() }

        wordListAdapter.isSavedLocally = true
        wordListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })

        binding.recyclerView.apply {
            adapter = commonAdapter
            itemAnimator = object : DefaultItemAnimator() {
                override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                    return true
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.filteredWords.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    is Resource.Success -> {
                        binding.swipeRefreshLayout.isRefreshing = false

                        if (it.data.isNullOrEmpty().not()) {
                            binding.messageEmptyVocab.visibility = View.GONE
                            commonAdapter.addAdapter(LEARN_ALL_ADAPTER_INDEX, learnAllWordsAdapter)
                        } else {
                            binding.messageEmptyVocab.visibility = View.VISIBLE
                            commonAdapter.removeAdapter(learnAllWordsAdapter)
                        }

                        wordListAdapter.submitList(it.data)
                        learnAllWordsAdapter.checkIfAllNeedToLearn(it.data)
                    }
                    is Resource.Error -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(context, it.error.message, Toast.LENGTH_SHORT).show()
                        Timber.e(it.error)
                    }
                }
            }
        }

        binding.addNewWordBtn.setOnClickListener {
            findNavController().navigate(R.id.to_add_new_word)
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
            WORD_MENU_ITEMS[3] -> {}
//                findNavController().navigate(MyWordsFragmentDirections.toAddNewWord(word)) todo refactor
            WORD_MENU_ITEMS[4] -> viewModel.delete(word)
        }
    }

    override fun postNotifyRecyclerView() {
        binding.recyclerView.post { wordListAdapter.notifyDataSetChanged() }
    }

}