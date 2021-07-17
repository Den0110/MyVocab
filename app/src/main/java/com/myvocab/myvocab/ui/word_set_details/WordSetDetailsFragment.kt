package com.myvocab.myvocab.ui.word_set_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.databinding.FragmentWordSetDetailsBinding
import com.myvocab.myvocab.domain.word_set_details.GetWordSetUseCase
import com.myvocab.myvocab.system.ResourceManager
import com.myvocab.myvocab.ui.word.BaseWordListFragment
import com.myvocab.myvocab.util.BaseViewModelFactory
import com.myvocab.myvocab.util.Resource
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject

class WordSetDetailsFragment : BaseWordListFragment() {

    private lateinit var binding: FragmentWordSetDetailsBinding

    @Inject
    lateinit var wordRepository: WordRepository

    @Inject
    lateinit var resourceManager: ResourceManager

    @Inject
    lateinit var getWordSetUseCase: GetWordSetUseCase

    override val viewModel: WordSetDetailsViewModel by viewModels {
        BaseViewModelFactory {
            WordSetDetailsViewModel(
                wordRepository,
                resourceManager,
                getWordSetUseCase,
                requireArguments()
            )
        }
    }

    private lateinit var menu: Menu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_word_set_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.includeToolbar.toolbar.inflateMenu(R.menu.word_set_details)
        menu = binding.includeToolbar.toolbar.menu

        binding.addList.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.dialog_sure_want_learn_this_word_list)
                .setPositiveButton(R.string.dialog_action_yes) { dialog, _ ->
                    viewModel.addWordSet()

                    // log saving word set
                    FirebaseAnalytics.getInstance(requireContext()).logEvent("save_word_set", Bundle().apply {
                        putString("title", viewModel.title.value)
                        putInt("size", viewModel.words.value.data?.size ?: 0)
                    })

                    dialog.dismiss()
                }
                .setNegativeButton(R.string.dialog_action_no) { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        binding.includeToolbar.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.remove -> {
                    AlertDialog.Builder(requireContext())
                        .setMessage(R.string.dialog_sure_want_remove_this_word_list)
                        .setPositiveButton(R.string.dialog_action_yes) { dialog, _ ->
                            viewModel.removeWordSet()

                            // log removing word set
                            FirebaseAnalytics.getInstance(requireContext()).logEvent("remove_word_set", Bundle().apply {
                                putString("title", viewModel.title.value)
                                putInt("size", viewModel.words.value.data?.size ?: 0)
                            })

                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.dialog_action_no) { dialog, _ -> dialog.dismiss() }
                        .create().show()
                }
            }
            true
        }

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.loadWordSet() }

        binding.recyclerView.apply {
            adapter = commonAdapter
            itemAnimator = object : DefaultItemAnimator() {
                override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                    return true
                }
            }
        }

        viewModel.isSavedLocally.observe(viewLifecycleOwner, {
            if (it) {
                showRemoveBtn()
            } else {
                showAddBtn()
            }
        })

        viewModel.title.observe(viewLifecycleOwner, { binding.includeToolbar.toolbar.title = it })
        viewModel.subtitle.observe(viewLifecycleOwner, { binding.includeToolbar.toolbar.subtitle = it })

        lifecycleScope.launchWhenStarted {
            viewModel.filteredWords.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                    is Resource.Success -> {
                        binding.swipeRefreshLayout.isRefreshing = false

                        wordListAdapter.isSavedLocally = viewModel.isSavedLocally.value
                        if (wordListAdapter.isSavedLocally == true && it.data.isNullOrEmpty().not()) {
                            learnAllWordsAdapter.checkIfAllNeedToLearn(it.data)
                            commonAdapter.addAdapter(LEARN_ALL_ADAPTER_INDEX, learnAllWordsAdapter)
                            binding.recyclerView.smoothScrollToPosition(0)
                        } else {
                            commonAdapter.removeAdapter(learnAllWordsAdapter)
                        }

                        wordListAdapter.submitList(it.data)
                    }
                    is Resource.Error -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(context, it.error.message, Toast.LENGTH_SHORT).show()
                        Timber.e(it.error)
                    }
                }
            }
        }

        viewModel.addingWordSet.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {
                        Timber.d("Adding \"${it.data?.title}\" word set")
                        binding.addList.isEnabled = false
                    }
                    is Resource.Success -> {
                        Snackbar.make(
                            binding.container, "${it.data.title} (${it.data.words.size} words) was added",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        viewModel.loadWordSet()
                    }
                    is Resource.Error -> {
                        Snackbar.make(binding.container, "Error, words weren't added", Snackbar.LENGTH_SHORT).show()
                        Timber.e(it.error)
                    }
                }
            }
        })

        viewModel.removingWordSet.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Loading -> {
                        Timber.d("Removing \"${it.data?.title}\" word set")
                        menu.findItem(R.id.remove)?.isEnabled = false
                    }
                    is Resource.Success -> {
                        Snackbar.make(
                            binding.container, "${it.data.title} (${it.data.words.size} words) was removed",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        viewModel.loadWordSet()
                    }
                    is Resource.Error -> {
                        Snackbar.make(binding.container, "Error, words weren't removed", Snackbar.LENGTH_SHORT).show()
                        Timber.e(it.error)
                    }
                }
            }
        })
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
        }
        return items.toTypedArray()
    }

    override fun onContextMenuItemClicked(item: String, word: Word) {
        when (item) {
            WORD_MENU_ITEMS[0] -> viewModel.markAsLearned(word)
            WORD_MENU_ITEMS[1] -> viewModel.resetProgress(word)
            WORD_MENU_ITEMS[2] -> viewModel.addToMyWords(word)
        }
    }

    override fun postNotifyRecyclerView() {
        binding.recyclerView.post { wordListAdapter.notifyDataSetChanged() }
    }

    private fun showAddBtn() {
        binding.addList.isVisible = true
        menu.findItem(R.id.remove)?.isVisible = false
        binding.addList.isEnabled = true
        menu.findItem(R.id.remove)?.isEnabled = true
    }

    private fun showRemoveBtn() {
        binding.addList.isVisible = false
        menu.findItem(R.id.remove)?.isVisible = true
        binding.addList.isEnabled = true
        menu.findItem(R.id.remove)?.isEnabled = true
    }

}
