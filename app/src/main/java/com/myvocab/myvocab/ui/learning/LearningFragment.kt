package com.myvocab.myvocab.ui.learning

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.Word
import com.myvocab.myvocab.databinding.FragmentLearningBinding
import com.myvocab.myvocab.ui.MainNavigationFragment
import com.myvocab.myvocab.util.*
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_learning.*
import javax.inject.Inject

class LearningFragment : MainNavigationFragment() {

    companion object {
        const val TRANSLATION_SHOW_FADE_ANIM_TIME = 200L
        const val TRANSLATION_SHOW_BOUNDS_ANIM_TIME = 300L
        const val TRANSLATION_HIDE_FADE_ANIM_TIME = 100L
        const val TRANSLATION_HIDE_BOUNDS_ANIM_TIME = 200L

        const val FAST_FADE_ANIM_TIME = 150L
        const val SLOW_FADE_ANIM_TIME = 250L
    }

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

    @SuppressLint("ObjectAnimatorBinding")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(LearningViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        hideTranslation()
        hideNoWordsView()
        hideNextViews()
        hideChooseViews()

        search_words_btn.setOnClickListener { findNavController().navigate(R.id.navigation_search) }

        viewModel.currentWord.observe(viewLifecycleOwner, Observer { word ->

            // show a new word (other fields bind in xml)
            showKnowingLevel(word)

            compositeDisposable.add(
                    animateNewWord(word).subscribe {
                        if (word != null) {
                            // ask user if them know the word
                            val askIfWordKnownDisposable =
                                    getAskObservable().subscribe { isKnown ->
                                        // show translation to check
                                        compositeDisposable.add(
                                                animateShowTranslation(word, isKnown).subscribe {
                                                    if (isKnown) {
                                                        // if answered that them know, ask is them was right
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
                                                        // else leave them to learn the new word
                                                        viewModel.zeroizeKnowingLevel()
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe {
                                                                    next_container.setOnClickListener {
                                                                        // when them finished memorizing, show next word
                                                                        viewModel.nextWord()
                                                                    }
                                                                }
                                                    }
                                                })
                                    }
                            compositeDisposable.clear()
                            compositeDisposable.add(askIfWordKnownDisposable)
                        }
                    })
        })
    }

    private fun getAskObservable(): Single<Boolean> = Single.create { emitter ->
        enableSelectItemBg(know_container, dont_know_container)
        know_container.setOnClickListener {
            emitter.onSuccess(true)
        }
        dont_know_container.setOnClickListener {
            emitter.onSuccess(false)
        }
    }

    private fun animateNewWord(word: Word?): Completable {
        var completable = Completable.complete()

        hideNextViews()

        completable = completable.mergeWith(Completable.create { emitter ->
            val hideTranslationAnim = TransitionSet().apply {
                addTransition(Fade(Fade.OUT).setDuration(TRANSLATION_HIDE_FADE_ANIM_TIME))
                addTransition(ChangeBounds().setDuration(TRANSLATION_HIDE_BOUNDS_ANIM_TIME))
                ordering = TransitionSet.ORDERING_SEQUENTIAL
                addTarget(translation_container)
                addTarget(examples_container)
                addTarget(word_container)

                addListener(rxTransitionCallback(emitter))
            }

            TransitionManager.beginDelayedTransition(word_scroll, hideTranslationAnim)
            hideTranslation()
        })

        completable = if (word != null) {
            completable
                    .mergeWith(animateShowWord())
                    .mergeWith(animateHideNoWordsView())
                    .mergeWith(animateShowDoesUserKnowViews())
        } else {
            completable
                    .mergeWith(animateHideWord())
                    .mergeWith(animateShowNoWordsView())
                    .mergeWith(animateHideChooseViews(FAST_FADE_ANIM_TIME))
        }

        return completable
    }

    private fun animateShowWord(): Completable {
        return Completable.create { emitter ->
            showWord()
            emitter.onComplete()
        }
    }

    private fun animateHideWord(): Completable {
        return Completable.create { emitter ->
            hideWord()
            emitter.onComplete()
        }
    }

    private fun animateShowTranslation(word: Word, isKnown: Boolean): Completable {
        var completable = Completable.complete()

        completable = completable.mergeWith(Completable.create { emitter ->
            val showTranslationAnim = TransitionSet().apply {
                addTransition(ChangeBounds().setDuration(TRANSLATION_SHOW_BOUNDS_ANIM_TIME))
                addTransition(Fade(Fade.IN).setDuration(TRANSLATION_SHOW_FADE_ANIM_TIME))
                ordering = TransitionSet.ORDERING_SEQUENTIAL
                addTarget(translation_container)
                addTarget(examples_container)
                addTarget(word_container)

                addListener(rxTransitionCallback(emitter))
            }

            TransitionManager.beginDelayedTransition(word_scroll, showTranslationAnim)
            showTranslation(word.translation?.toLowerCase())
        })

        completable = if (isKnown) {
            completable.mergeWith(animateShowWasUserRightViews())
        } else {
            completable.mergeWith(
                    animateHideChooseViews(SLOW_FADE_ANIM_TIME)
                            .andThen(animateShowNextViews())
            )
        }

        return completable
    }

    private fun animateShowDoesUserKnowViews(): Completable {
        return animateHideChooseViews(FAST_FADE_ANIM_TIME).andThen(Completable.create {
            showDoesUserKnowViews()
            disableSelectItemBg(know_container, dont_know_container)
            it.onComplete()
        }).andThen(Completable.create { emitter ->

            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    question.alpha = value
                    choose_views.alpha = value
                }
                addListener(rxAnimatorCallback(emitter))
                duration = FAST_FADE_ANIM_TIME
                interpolator = AccelerateInterpolator()
            }.start()
        })
    }

    private fun animateShowWasUserRightViews(): Completable {
        return animateHideChooseViews(SLOW_FADE_ANIM_TIME).andThen(Completable.create {
            showWasUserRight()
            disableSelectItemBg(know_container, dont_know_container)
            it.onComplete()
        }).andThen(Completable.create { emitter ->

            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    question.alpha = value
                    choose_views.alpha = value
                }
                addListener(rxAnimatorCallback(emitter))
                duration = SLOW_FADE_ANIM_TIME
                interpolator = AccelerateInterpolator()
            }.start()
        })
    }

    private fun animateHideChooseViews(dur: Long): Completable {
        return Completable.create { emitter ->
            if(choose_views.visibility != View.GONE) {
                ValueAnimator.ofFloat(1f, 0f).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        question.alpha = value
                        choose_views.alpha = value
                    }
                    addListener(rxAnimatorCallback(emitter))
                    duration = dur
                    interpolator = DecelerateInterpolator()
                }.start()
            } else {
                emitter.onComplete()
            }
        }.doOnComplete {
            hideChooseViews()
        }
    }

    private fun animateShowNextViews(): Completable {
        return Completable.create { emitter ->
            if(next_container.visibility != View.VISIBLE) {
                showNextViews()

                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = SLOW_FADE_ANIM_TIME
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        question.alpha = value
                        choose_views.alpha = value
                    }
                    addListener(rxAnimatorCallback(emitter))
                }.start()
            } else {
                emitter.onComplete()
            }
        }
    }

    private fun animateShowNoWordsView(): Completable {
        return Completable.create {
            if(no_words_container.visibility != View.VISIBLE) {
                showNoWordsView()
                ObjectAnimator
                        .ofFloat(no_words_container, View.ALPHA, 0f, 1f)
                        .addListener(rxAnimatorCallback(it))
            } else {
                it.onComplete()
            }
        }
    }

    private fun animateHideNoWordsView(): Completable {
        return Completable.create {
            hideNoWordsView()
            it.onComplete()
        }
    }

    private fun showWord(){
        word_scroll.visibility = View.VISIBLE
    }

    private fun hideWord(){
        word_scroll.visibility = View.GONE
    }

    private fun showKnowingLevel(word: Word?) {
        knowing_level.setImageDrawable(when (word?.knowingLevel) {
            0 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_empty_24dp)
            1 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_red_24dp)
            2 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_yellow_24dp)
            3 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_green_24dp)
            else -> null
        })
    }

    private fun showTranslation(text: String?) {
        translation.text = text
        translation_container.visibility = View.VISIBLE
    }

    private fun hideTranslation() {
        translation_container.visibility = View.GONE
    }

    private fun showDoesUserKnowViews(){
        question.visibility = View.VISIBLE
        question.text = getString(R.string.do_you_know)
        choose_views.visibility = View.VISIBLE
        choose_views.isEnabled = true
        know_icon.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_done_48dp))
        dont_know_icon.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_close_48dp))
        know_label.text = getString(R.string.i_know)
        dont_know_label.text = getString(R.string.i_dont_know)
    }

    private fun showWasUserRight(){
        question.visibility = View.VISIBLE
        question.text = getString(R.string.was_you_right)
        choose_views.visibility = View.VISIBLE
        choose_views.isEnabled = true
        know_icon.setImageDrawable(when (viewModel.currentWord.value?.knowingLevel?.plus(1)) {
            1 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_red_48dp)
            2 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_yellow_48dp)
            3 -> ContextCompat.getDrawable(context!!, R.drawable.ic_brain_green_48dp)
            else -> null
        })
        dont_know_icon.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_brain_empty_48dp))
        know_label.text = getString(R.string.i_was_right)
        dont_know_label.text = getString(R.string.i_was_wrong)
    }

    private fun hideChooseViews(){
        question.visibility = View.INVISIBLE
        choose_views.visibility = View.GONE
        choose_views.isEnabled = false
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
        no_words_container.visibility = View.VISIBLE
    }

    private fun hideNoWordsView(){
        no_words_container.visibility = View.GONE
    }

}