package com.myvocab.myvocab.ui.word

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.myvocab.myvocab.R
import com.myvocab.myvocab.ui.MainNavigationFragment
import kotlinx.android.synthetic.main.fragment_word_set_details.*
import javax.inject.Inject

abstract class BaseWordListFragment : MainNavigationFragment() {

    private val WORD_MENU_ITEMS: Array<String> by lazy { arrayOf(
            getString(R.string.mark_as_learned),
            getString(R.string.reset_the_progress),
            getString(R.string.add_to_my_vocab),
            getString(R.string.delete_word)
    ) }

    abstract val viewModel: BaseWordListViewModel

    @Inject
    lateinit var wordListAdapter: WordListAdapter

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
                        wordListAdapter.currentList.forEach {
                            it.needToLearn = state
                            viewModel.update(it)
                        }
                        wordListAdapter.needToLearnAll = state
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.dialog_action_no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        recycler_view.post { wordListAdapter.notifyDataSetChanged() }
                    }
                    .create().show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordListAdapter.callback = viewModel.wordCallback
        wordListAdapter.learnAllCallback = learnAllCallback

        viewModel.showWordDialogEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { wordData ->
                val word = wordData.first
                val isSavedLocally = wordData.second

                val items = arrayListOf<String>()

                if (!isSavedLocally) {
                    items.add(WORD_MENU_ITEMS[2]) // add to my words
                } else {
                    if (word.knowingLevel < 3)
                        items.add(WORD_MENU_ITEMS[0]) // mark as learned

                    if (word.knowingLevel > 0)
                        items.add(WORD_MENU_ITEMS[1]) // reset the progress

                    if (word.wordSetId == "my_words")
                        items.add(WORD_MENU_ITEMS[3]) // delete
                }

                AlertDialog.Builder(context!!)
                        .setTitle(wordData.first.word)
                        .setItems(items.toTypedArray()) { _, index ->
                            when (items[index]) {
                                WORD_MENU_ITEMS[0] -> viewModel.markAsLearned(word)
                                WORD_MENU_ITEMS[1] -> viewModel.resetProgress(word)
                                WORD_MENU_ITEMS[2] -> viewModel.addToMyWords(word)
                                WORD_MENU_ITEMS[3] -> viewModel.delete(word)
                            }
                        }
                        .setNegativeButton(R.string.dialog_action_cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create().show()
            }
        })

        viewModel.notifyWordChangedEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { index ->
                wordListAdapter.notifyItemChanged(index + 1)
            }
        })

        viewModel.addToMyWordsResultEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { wordData ->
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
        })

        viewModel.notifyWordRemovedEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { index ->
                if (index == -1) {
                    Snackbar.make(view, "Error, word wasn't deleted", Snackbar.LENGTH_SHORT).show()
                } else {
                    wordListAdapter.notifyItemRemoved(index + 1)
                }
            }
        })

    }

}