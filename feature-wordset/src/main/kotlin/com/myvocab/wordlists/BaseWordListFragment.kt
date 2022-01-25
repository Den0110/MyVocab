package com.myvocab.wordlists

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.snackbar.Snackbar
import com.myvocab.commonui.MainNavigationFragment
import com.myvocab.domain.entities.Word
import com.myvocab.wordlists.word.LearnAllCallback
import com.myvocab.wordlists.word.LearnAllWordsAdapter
import com.myvocab.wordlists.word.SearchAdapter
import com.myvocab.wordlists.word.WordListAdapter
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

abstract class BaseWordListFragment : MainNavigationFragment() {

    companion object {
        const val LEARN_ALL_ADAPTER_INDEX = 1
    }

    val WORD_MENU_ITEMS: Array<String> by lazy {
        arrayOf(
            getString(R.string.mark_as_learned),
            getString(R.string.reset_the_progress),
            getString(R.string.add_to_my_vocab),
            getString(R.string.edit_word),
            getString(R.string.delete_word)
        )
    }

    abstract val viewModel: BaseWordListViewModel

    @Inject
    lateinit var wordListAdapter: WordListAdapter

    @Inject
    lateinit var learnAllWordsAdapter: LearnAllWordsAdapter

    private val searchAdapter by lazy {
        SearchAdapter({
            viewModel.onSearchFilterChanged(it)
        }, {
            viewModel.onSortTypeChanged(it)
        })
    }

    lateinit var commonAdapter: ConcatAdapter

    private val learnAllCallback = object : LearnAllCallback() {
        override fun onNeedToLearnAll(state: Boolean) {
            val title =
                if (state)
                    getString(R.string.dialog_select_all_words_to_learn)
                else
                    getString(R.string.dialog_deselect_all_words_to_learn)
            AlertDialog.Builder(context!!)
                .setMessage(title)
                .setPositiveButton(R.string.dialog_action_yes) { dialog, _ ->
                    viewModel.applyNeedToLearnState(wordListAdapter.currentList, state)
                    learnAllWordsAdapter.needToLearnAll = state
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.dialog_action_no) { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    postNotifyRecyclerView()
                }
                .create().show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commonAdapter = ConcatAdapter(searchAdapter, wordListAdapter)

        wordListAdapter.callback = viewModel.wordCallback
        learnAllWordsAdapter.learnAllCallback = learnAllCallback

        lifecycleScope.launchWhenStarted {
            viewModel.searchFilter.collectLatest {
                searchAdapter.searchText = it
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.sortType.collectLatest {
                searchAdapter.selectedSortType = it
            }
        }

        viewModel.showWordDialogEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { wordData ->
                val word = wordData.first
                val isSavedLocally = wordData.second

                val items = getContextMenuItems(word, isSavedLocally)

                AlertDialog.Builder(requireContext())
                    .setTitle(wordData.first.word)
                    .setItems(items) { _, index ->
                        onContextMenuItemClicked(items[index], word)
                    }
                    .setNegativeButton(R.string.dialog_action_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
            }
        })

        lifecycleScope.launchWhenStarted {
            viewModel.addToMyWordsResultEvent.collectLatest { wordData ->
                val word = wordData.first
                val isAdded = wordData.second
                Snackbar.make(
                    view,
                    if (isAdded) {
                        "\"${word.word}\" is added to your word list"
                    } else {
                        "Error, word wasn't added"
                    },
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.wordDeleteError.collectLatest {
                Snackbar.make(view, "Error, word wasn't deleted", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    abstract fun getContextMenuItems(word: Word, isSavedLocally: Boolean): Array<String>

    abstract fun onContextMenuItemClicked(item: String, word: Word)

    abstract fun postNotifyRecyclerView()

}