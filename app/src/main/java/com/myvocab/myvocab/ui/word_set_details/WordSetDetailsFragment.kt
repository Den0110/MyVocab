package com.myvocab.myvocab.ui.word_set_details

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.databinding.FragmentWordSetDetailsBinding
import com.myvocab.myvocab.domain.word_set_details.GetWordSetUseCase
import com.myvocab.myvocab.ui.word.BaseWordListFragment
import com.myvocab.myvocab.util.Resource
import com.myvocab.myvocab.util.getViewModel
import kotlinx.android.synthetic.main.fragment_word_set_details.*
import kotlinx.android.synthetic.main.fragment_word_set_details.recycler_view
import kotlinx.android.synthetic.main.fragment_word_set_details.swipe_refresh_layout
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject

class WordSetDetailsFragment : BaseWordListFragment() {

    private lateinit var binding: FragmentWordSetDetailsBinding

    @Inject
    lateinit var wordRepository: WordRepository
    @Inject
    lateinit var getWordSetUseCase: GetWordSetUseCase

    override val viewModel: WordSetDetailsViewModel
            by lazy { getViewModel { WordSetDetailsViewModel(wordRepository, getWordSetUseCase, arguments!!, context!!) } }

    private lateinit var menu: Menu

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_word_set_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.word_set_details)
        menu = toolbar.menu

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    AlertDialog.Builder(context!!)
                            .setMessage(R.string.dialog_sure_want_learn_this_word_list)
                            .setPositiveButton(R.string.dialog_action_yes) { dialog, _ ->
                                viewModel.addWordSet()

                                // log saving word set
                                FirebaseAnalytics.getInstance(context!!).logEvent("save_word_set", Bundle().apply {
                                    putString("title", viewModel.title.value)
                                    putInt("size", viewModel.words.value?.data?.size ?: 0)
                                })

                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.dialog_action_no) { dialog, _ -> dialog.dismiss() }
                            .create().show()
                }
                R.id.remove -> {
                    AlertDialog.Builder(context!!)
                            .setMessage(R.string.dialog_sure_want_remove_this_word_list)
                            .setPositiveButton(R.string.dialog_action_yes) { dialog, _ ->
                                viewModel.removeWordSet()

                                // log removing word set
                                FirebaseAnalytics.getInstance(context!!).logEvent("remove_word_set", Bundle().apply {
                                    putString("title", viewModel.title.value)
                                    putInt("size", viewModel.words.value?.data?.size ?: 0)
                                })

                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.dialog_action_no) { dialog, _ -> dialog.dismiss() }
                            .create().show()
                }
            }
            true
        }

        swipe_refresh_layout.setOnRefreshListener { viewModel.loadWordSet() }

        recycler_view.adapter = wordListAdapter

        viewModel.isSavedLocally.observe(viewLifecycleOwner, Observer {
            if (it) {
                showRemoveBtn()
            } else {
                showAddBtn()
            }
        })

        viewModel.title.observe(viewLifecycleOwner, Observer { toolbar.title = it })
        viewModel.subtitle.observe(viewLifecycleOwner, Observer { toolbar.subtitle = it })

        viewModel.words.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                }
                Resource.Status.SUCCESS -> {
                    swipe_refresh_layout.isRefreshing = false
                    wordListAdapter.isSavedLocally = viewModel.isSavedLocally.value
                    if (wordListAdapter.isSavedLocally == true)
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

        viewModel.addingWordSet.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        Timber.d("Adding \"${it.data?.title}\" word set")
                        menu.findItem(R.id.add)?.isEnabled = false
                    }
                    Resource.Status.SUCCESS -> {
                        Snackbar.make(container, "${it.data?.title} (${it.data?.words?.size} words) was added",
                                Snackbar.LENGTH_SHORT).show()
                        viewModel.loadWordSet()
                    }
                    Resource.Status.ERROR -> {
                        Snackbar.make(container, "Error, words weren't added", Snackbar.LENGTH_SHORT).show()
                        Timber.e(it.error)
                    }
                }
            }
        })

        viewModel.removingWordSet.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        Timber.d("Removing \"${it.data?.title}\" word set")
                        menu.findItem(R.id.remove)?.isEnabled = false
                    }
                    Resource.Status.SUCCESS -> {
                        Snackbar.make(container, "${it.data?.title} (${it.data?.words?.size} words) was removed",
                                Snackbar.LENGTH_SHORT).show()
                        viewModel.loadWordSet()
                    }
                    Resource.Status.ERROR -> {
                        Snackbar.make(container, "Error, words weren't removed", Snackbar.LENGTH_SHORT).show()
                        Timber.e(it.error)
                    }
                }
            }
        })
    }

    private fun showAddBtn() {
        menu.findItem(R.id.add)?.isVisible = true
        menu.findItem(R.id.remove)?.isVisible = false
        menu.findItem(R.id.add)?.isEnabled = true
        menu.findItem(R.id.remove)?.isEnabled = true
    }

    private fun showRemoveBtn() {
        menu.findItem(R.id.add)?.isVisible = false
        menu.findItem(R.id.remove)?.isVisible = true
        menu.findItem(R.id.add)?.isEnabled = true
        menu.findItem(R.id.remove)?.isEnabled = true
    }

}
