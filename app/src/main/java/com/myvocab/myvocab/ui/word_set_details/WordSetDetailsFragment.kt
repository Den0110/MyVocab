package com.myvocab.myvocab.ui.word_set_details

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.source.WordRepository
import com.myvocab.myvocab.databinding.FragmentWordSetDetailsBinding
import com.myvocab.myvocab.domain.word_set_details.LoadWordSetUseCase
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.ui.word.WordListAdapter
import com.myvocab.myvocab.util.Resource
import com.myvocab.myvocab.util.getViewModel
import kotlinx.android.synthetic.main.fragment_word_set_details.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject

class WordSetDetailsFragment : MainNavigationFragment() {

    companion object {
        private const val TAG = "WordSetDetailsActivity"
    }

    private lateinit var binding: FragmentWordSetDetailsBinding

    @Inject
    lateinit var wordRepository: WordRepository
    @Inject
    lateinit var loadWordSetUseCase: LoadWordSetUseCase

    private val viewModel: WordSetDetailsViewModel
            by lazy { getViewModel { WordSetDetailsViewModel(wordRepository, loadWordSetUseCase, arguments!!, context!!) } }

    @Inject
    lateinit var wordListAdapter: WordListAdapter

    private lateinit var menu: Menu

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_word_set_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = viewModel.initialWordSet.title
        toolbar.inflateMenu(R.menu.word_set_details)
        menu = toolbar.menu
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    AlertDialog.Builder(context!!)
                            .setMessage("Are you sure want to add this word list to your vocab?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                viewModel.addWordSet()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                            .create().show()
                }
                R.id.remove -> {
                    AlertDialog.Builder(context!!)
                            .setMessage("Are you sure want to remove this word list from your vocab?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                viewModel.removeWordSet()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                            .create().show()
                }
            }
            true
        }

        recycler_view.adapter = wordListAdapter

        swipe_refresh_layout.setOnRefreshListener { viewModel.loadWordSet() }

        viewModel.isSavedLocally.observe(viewLifecycleOwner, Observer {
            if (it) {
                showRemoveBtn()
            } else {
                showAddBtn()
            }
        })

        viewModel.subtitle.observe(viewLifecycleOwner, Observer { toolbar.subtitle = it })

        viewModel.wordSet.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                }
                Resource.Status.SUCCESS -> {
                    toolbar.title = it.data!!.title
                    wordListAdapter.submitList(it.data.words)
                    swipe_refresh_layout.isRefreshing = false
                }
                Resource.Status.ERROR -> {
                    swipe_refresh_layout.isRefreshing = false
                    Toast.makeText(context, it.error?.message, Toast.LENGTH_SHORT).show()
                    Timber.e(it.error)
                }
            }
        })

        viewModel.addingWordSet.observe(viewLifecycleOwner, Observer {
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
        })

        viewModel.removingWordSet.observe(viewLifecycleOwner, Observer {
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
