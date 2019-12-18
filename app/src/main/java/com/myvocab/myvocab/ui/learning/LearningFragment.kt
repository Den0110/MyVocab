package com.myvocab.myvocab.ui.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myvocab.myvocab.R
import com.myvocab.myvocab.databinding.FragmentLearningBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_learning.*
import javax.inject.Inject

class LearningFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentLearningBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: LearningViewModel

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_learning, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(LearningViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        hideTranslation()
        hideNoWordsView()
        hideNextViews()
        hideChooseViews()

        viewModel.currentWord.observe(viewLifecycleOwner, Observer {
            hideTranslation()
            hideNextViews()
            if(it != null) {
                knowing_level.setImageDrawable(when(it.knowingLevel){
                    0 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_empty_24dp)
                    1 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_red_24dp)
                    2 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_yellow_24dp)
                    3 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_green_24dp)
                    else -> null
                })
                val askIfWordKnownDisposable =
                        getAskObservable()
                                .subscribe { isKnown ->
                                    showTranslation()
                                    hideChooseViews()
                                    if (isKnown) {
                                        showWasUserRightViews()
                                        getAskObservable().subscribe { isRight ->
                                            if (isRight) {
                                                viewModel.increaseKnowingLevel()
                                            } else {
                                                viewModel.zeroizeKnowingLevel()
                                            }.subscribe {
                                                viewModel.nextWord()
                                            }
                                        }
                                    } else {
                                        showNextViews()
                                        viewModel.zeroizeKnowingLevel()
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe {
                                            next_container.setOnClickListener {
                                                viewModel.nextWord()
                                            }
                                        }
                                    }
                                }
                compositeDisposable.clear()
                compositeDisposable.add(askIfWordKnownDisposable)
                hideNoWordsView()
                showDoesUserKnowViews()
            } else {
                knowing_level.setImageDrawable(null)
                showNoWordsView()
                hideChooseViews()
            }
        })
    }

    private fun getAskObservable(): Single<Boolean> = Single.create<Boolean> { emitter ->
        know_container.setOnClickListener {
            emitter.onSuccess(true)
        }
        dont_know_container.setOnClickListener {
            emitter.onSuccess(false)
        }
    }

    private fun showDoesUserKnowViews() {
        question.visibility = View.VISIBLE
        question.text = getString(R.string.do_you_know)
        knowing_views.visibility = View.VISIBLE
        knowing_views.isEnabled = true
        know_icon.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_done_48dp))
        dont_know_icon.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_close_48dp))
        know_label.text = getString(R.string.i_know)
        dont_know_label.text = getString(R.string.i_dont_know)
    }

    private fun showWasUserRightViews() {
        question.visibility = View.VISIBLE
        question.text = getString(R.string.was_you_right)
        knowing_views.visibility = View.VISIBLE
        knowing_views.isEnabled = true
        know_icon.setImageDrawable(when(viewModel.currentWord.value?.knowingLevel?.plus(1)){
            1 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_red_48dp)
            2 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_yellow_48dp)
            3 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_green_48dp)
            else -> null
        })
        dont_know_icon.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_brain_empty_48dp))
        know_label.text = getString(R.string.i_was_right)
        dont_know_label.text = getString(R.string.i_was_wrong)
    }

    private fun hideChooseViews() {
        question.visibility = View.INVISIBLE
        knowing_views.visibility = View.INVISIBLE
        knowing_views.isEnabled = false
    }

    private fun showTranslation() {
        translation.visibility = View.VISIBLE
    }

    private fun hideTranslation() {
        translation.visibility = View.INVISIBLE
    }

    private fun showNextViews() {
        question.visibility = View.VISIBLE
        question.text = getString(R.string.memorise_the_translation)
        next_container.visibility = View.VISIBLE
    }

    private fun hideNextViews() {
        question.visibility = View.INVISIBLE
        next_container.visibility = View.GONE
    }

    private fun showNoWordsView() {
        no_words_view.visibility = View.VISIBLE
    }

    private fun hideNoWordsView() {
        no_words_view.visibility = View.GONE
    }

}