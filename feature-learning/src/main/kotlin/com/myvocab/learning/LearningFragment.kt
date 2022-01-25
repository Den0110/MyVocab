package com.myvocab.learning

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.myvocab.commonui.MainNavigationFragment
import com.myvocab.core.util.*
import com.myvocab.domain.entities.Word
import com.myvocab.domain.entities.Word.Example.Companion.getHighlight
import com.myvocab.domain.entities.Word.Example.Companion.getRawText
import com.myvocab.learning.databinding.FragmentLearningBinding
import com.myvocab.learning.databinding.LearningWordExampleBinding
import com.myvocab.navigation.NavigationFlow
import com.myvocab.navigation.Navigator
import io.reactivex.Completable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class LearningFragment : MainNavigationFragment() {

    companion object {
        const val TRANSLATION_SHOW_FADE_ANIM_TIME = 200L
        const val TRANSLATION_SHOW_BOUNDS_ANIM_TIME = 300L

        const val FAST_FADE_ANIM_TIME = 150L
        const val SLOW_FADE_ANIM_TIME = 250L
    }

    private lateinit var binding: FragmentLearningBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: LearningViewModel

    @Inject
    lateinit var navigator: Navigator

    private var interstitialAd: InterstitialAd? = null
    private var thisSessionShowedWordNumber = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        binding.searchWordsBtn.setOnClickListener { navigator.navigateToFlow(NavigationFlow.WordLists) }

        viewModel.currentWord.observe(viewLifecycleOwner, { word ->
            lifecycleScope.launchWhenStarted {
                updateWord(word)

                animateNewWord(word).await()
                if (word != null) {
                    val isUserKnowWord = askUser()

                    animateShowTranslation(word, isUserKnowWord)

                    if (isUserKnowWord) {
                        val isUserWasRight = askUser()

                        if (isUserWasRight) {
                            viewModel.increaseKnowingLevel()
                        } else {
                            viewModel.zeroizeKnowingLevel()
                        }
                        viewModel.nextWord()

                        logRightness(word, isUserWasRight)
                    } else {
                        viewModel.zeroizeKnowingLevel()
                        binding.nextContainer.setOnClickListener {
                            viewModel.nextWord()
                        }
                    }

                    logWordKnowing(word, isUserKnowWord)
                }

                word?.let { logDisplayingWord(it) }
            }
        })

        viewModel.showedWordNumber.observe(viewLifecycleOwner, {

            val showAd = Firebase.remoteConfig.getLong("learning_ad_show_time").toInt()
            val loadAd = Firebase.remoteConfig.getLong("learning_ad_load_time").toInt()
            val minCurrentSessionWordNumber =
                Firebase.remoteConfig.getLong("learning_ad_min_session_show_time").toInt()

            when {
                it >= showAd -> {
                    if (thisSessionShowedWordNumber >= minCurrentSessionWordNumber) {
                        if (!BuildConfig.DEBUG)
                            showInterstitialAd {
                                viewModel.showedWordNumber.value = 0
                            }
                    }
                }
                it == loadAd -> loadInterstitialAd()
            }
            thisSessionShowedWordNumber++
        })

    }

    @SuppressLint("MissingPermission")
    private fun logDisplayingWord(it: Word) {
        FirebaseAnalytics.getInstance(requireContext())
            .logEvent("show_next_word", Bundle().apply {
                putString("text", it.word)
                putInt("length", it.word.length)
                viewModel.wordSetTitle.value?.let {
                    putString("word_set_title", it)
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun logRightness(word: Word, wasRight: Boolean) {
        FirebaseAnalytics.getInstance(requireContext())
            .logEvent("rightness", Bundle().apply {
                putString("text", word.word)
                putInt("length", word.word.length)
                putBoolean("was_right", wasRight)
            })
    }

    @SuppressLint("MissingPermission")
    private fun logWordKnowing(word: Word, isKnown: Boolean) {
        FirebaseAnalytics.getInstance(requireContext())
            .logEvent("knowing_word", Bundle().apply {
                putString("text", word.word)
                putInt("length", word.word.length)
                putBoolean("know", isKnown)
            })
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),
            getString(R.string.admob_learning_interstitial_ad_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.d(adError.message)
                    interstitialAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Timber.d("Ad was loaded.")
                    interstitialAd = ad
                }
            })
    }

    private fun showInterstitialAd(onShowed: () -> Unit) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Timber.d("The ad was dismissed")
                interstitialAd = null
                loadInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                interstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Timber.d("Ad showed fullscreen content.")
            }
        }

        if (interstitialAd != null) {
            interstitialAd?.show(requireActivity())
            onShowed()
        } else {
            loadInterstitialAd()
        }
    }

    private suspend fun askUser() = callbackFlow {
        enableSelectItemBg(binding.knowContainer, binding.dontKnowContainer)
        binding.knowContainer.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                trySend(true)
            }
        }
        binding.dontKnowContainer.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                trySend(false)
            }
        }
        awaitClose { }
    }.first()

    private fun animateNewWord(word: Word?): Completable {
        var completable = Completable.complete()

        hideNextViews()
        hideTranslation()

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

    private suspend fun animateShowTranslation(word: Word, isKnown: Boolean) = coroutineScope {
        val showTranslationAnim = TransitionSet().apply {
            addTransition(ChangeBounds().setDuration(TRANSLATION_SHOW_BOUNDS_ANIM_TIME))
            addTransition(Fade(Fade.IN).setDuration(TRANSLATION_SHOW_FADE_ANIM_TIME))
            ordering = TransitionSet.ORDERING_SEQUENTIAL
            addTarget(binding.translationContainer)
            addTarget(binding.examplesContainer)
            addTarget(binding.wordContainer)
        }

        val showTranslation = async {
            showTranslationAnim.waitUntilFinished {
                TransitionManager.beginDelayedTransition(binding.wordScroll, showTranslationAnim)
                showTranslation(word.translation.lowercase(Locale.getDefault()))
                showExampleTranslations(word)
            }
        }

        if (isKnown) {
            val showResult = async { animateShowWasUserRightViews().await() }
            awaitAll(showTranslation, showResult)
        } else {
            val hideChooseViews = async {
                animateHideChooseViews(SLOW_FADE_ANIM_TIME).await()
                animateShowNextViews().await()
            }
            awaitAll(showTranslation, hideChooseViews)
        }
    }

    private fun animateShowDoesUserKnowViews(): Completable {
        return animateHideChooseViews(FAST_FADE_ANIM_TIME).andThen(Completable.create {
            showDoesUserKnowViews()
            disableSelectItemBg(binding.knowContainer, binding.dontKnowContainer)
            it.onComplete()
        }).andThen(Completable.create { emitter ->

            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.question.alpha = value
                    binding.chooseViews.alpha = value
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
            disableSelectItemBg(binding.knowContainer, binding.dontKnowContainer)
            it.onComplete()
        }).andThen(Completable.create { emitter ->

            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.question.alpha = value
                    binding.chooseViews.alpha = value
                }
                addListener(rxAnimatorCallback(emitter))
                duration = SLOW_FADE_ANIM_TIME
                interpolator = AccelerateInterpolator()
            }.start()
        })
    }

    private fun animateHideChooseViews(dur: Long): Completable {
        return Completable.create { emitter ->
            if (binding.chooseViews.visibility != View.GONE) {
                ValueAnimator.ofFloat(1f, 0f).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        binding.question.alpha = value
                        binding.chooseViews.alpha = value
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
            if (binding.nextContainer.visibility != View.VISIBLE) {
                showNextViews()

                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = SLOW_FADE_ANIM_TIME
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        binding.question.alpha = value
                        binding.chooseViews.alpha = value
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
            if (binding.noWordsContainer.visibility != View.VISIBLE) {
                showNoWordsView()
                ObjectAnimator
                    .ofFloat(binding.noWordsContainer, View.ALPHA, 0f, 1f)
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

    private fun showWord() {
        binding.wordScroll.visibility = View.VISIBLE
    }

    private fun hideWord() {
        binding.wordScroll.visibility = View.GONE
    }

    private fun updateWord(word: Word?) {
        setKnowingLevel(word)
        setTitle(word)
        setMeanings(word)
        setSynonyms(word)
        setExamples(word)
    }

    private fun setKnowingLevel(word: Word?) {
        binding.knowingLevel.setImageDrawable(
            when (word?.knowingLevel) {
                0 -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_brain_empty_24dp)
                1 -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_brain_red_24dp)
                2 -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_brain_yellow_24dp)
                3 -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_brain_green_24dp)
                else -> null
            }
        )
    }

    private fun setTitle(word: Word?) {
        word?.word?.let { w ->
            val wordTitle = SpannableStringBuilder(w.lowercase(Locale.getDefault()))

            if (word.transcription.isNotEmpty()) {
                val ts = " [${word.transcription}]".replace("ˈ", "'")
                wordTitle.append(ts)

                val tsStart = wordTitle.indexOf(ts)
                val tsEnd = tsStart + ts.length

                wordTitle.setSpan(
                    AbsoluteSizeSpan(spToPixels(16)),
                    tsStart,
                    tsEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                wordTitle.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.secondaryTextColor
                        )
                    ),
                    tsStart, tsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            binding.wordTitle.text = wordTitle
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                binding.wordTitle.breakStrategy = LineBreaker.BREAK_STRATEGY_HIGH_QUALITY
            }
        }
    }

    private fun setMeanings(word: Word?) {
        if (!word?.meanings.isNullOrEmpty()) {
            binding.meanings.text = word!!.meanings.joinToString(", ", postfix = ", …")
            binding.meanings.visibility = View.VISIBLE
        } else {
            binding.meanings.visibility = View.GONE
        }
    }

    private fun setSynonyms(word: Word?) {
        if (!word?.synonyms.isNullOrEmpty()) {
            binding.synonyms.text = word!!.synonyms.joinToString(", ", postfix = ", …")
            binding.synonyms.visibility = View.VISIBLE
        } else {
            binding.synonyms.visibility = View.GONE
        }
    }

    private fun setExamples(word: Word?) {
        binding.examplesContainer.removeAllViews()
        if (!word?.examples.isNullOrEmpty()) {
            word?.examples!!.forEach {
                val exampleView = LearningWordExampleBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    binding.examplesContainer,
                    false
                )

                val text = SpannableStringBuilder(getRawText(it.text))
                val textHighlight = getHighlight(it.text)

                textHighlight?.let {
                    val textMaskStart = textHighlight.first
                    val textMaskEnd = textHighlight.last + 1

                    text.setSpan(
                        StyleSpan(Typeface.BOLD),
                        textMaskStart,
                        textMaskEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    text.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.exampleTextColor
                            )
                        ),
                        textMaskStart, textMaskEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                exampleView.text.text = text

                val starMask = "*****"
                var translation = SpannableStringBuilder(getRawText(it.translation))
                val starHighlight = getHighlight(it.translation)

                starHighlight?.let {
                    translation =
                        SpannableStringBuilder(translation.replaceRange(starHighlight, starMask))

                    val translationMaskStart = starHighlight.first
                    val translationMaskEnd = starHighlight.first + starMask.length

                    translation.setSpan(
                        StyleSpan(Typeface.BOLD),
                        translationMaskStart,
                        translationMaskEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    translation.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.exampleTranslationColor
                            )
                        ),
                        translationMaskStart, translationMaskEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                exampleView.translation.text = translation

                binding.examplesContainer.addView(exampleView.root)
            }
            binding.examplesContainer.visibility = View.VISIBLE
        } else {
            binding.examplesContainer.visibility = View.GONE
        }
    }

    private fun showTranslation(text: String?) {
        binding.translation.text = text
        binding.translationContainer.visibility = View.VISIBLE
    }

    private fun hideTranslation() {
        binding.translationContainer.visibility = View.GONE
    }

    private fun showExampleTranslations(word: Word?) {
        word?.let {
            binding.examplesContainer.children.forEachIndexed { index, view ->

                val example = word.examples[index]
                val translation = SpannableStringBuilder(getRawText(example.translation))
                val translationHighlight = getHighlight(example.translation)

                translationHighlight?.let {
                    val translationMaskStart = it.first
                    val translationMaskEnd = it.last + 1

                    translation.setSpan(
                        StyleSpan(Typeface.BOLD),
                        translationMaskStart,
                        translationMaskEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    translation.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.exampleTranslationColor
                            )
                        ),
                        translationMaskStart, translationMaskEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                view.findViewById<TextView>(R.id.translation).text = translation
            }
        }
    }

    private fun showDoesUserKnowViews() {
        binding.question.visibility = View.VISIBLE
        binding.question.text = getString(R.string.do_you_know)
        binding.chooseViews.visibility = View.VISIBLE
        binding.chooseViews.isEnabled = true
        binding.knowIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_done_48dp
            )
        )
        binding.dontKnowIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_close_48dp
            )
        )
        binding.knowLabel.text = getString(R.string.i_know)
        binding.dontKnowLabel.text = getString(R.string.i_dont_know)
    }

    private fun showWasUserRight() {
        binding.question.visibility = View.VISIBLE
        binding.question.text = getString(R.string.was_you_right)
        binding.chooseViews.visibility = View.VISIBLE
        binding.chooseViews.isEnabled = true
        binding.knowIcon.setImageDrawable(
            when (viewModel.currentWord.value?.knowingLevel?.plus(1)) {
                1 -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_brain_red_48dp)
                2 -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_brain_yellow_48dp)
                3 -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_brain_green_48dp)
                else -> null
            }
        )
        binding.dontKnowIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_brain_empty_48dp
            )
        )
        binding.knowLabel.text = getString(R.string.i_was_right)
        binding.dontKnowLabel.text = getString(R.string.i_was_wrong)
    }

    private fun hideChooseViews() {
        binding.question.visibility = View.INVISIBLE
        binding.chooseViews.visibility = View.GONE
        binding.chooseViews.isEnabled = false
    }

    private fun showNextViews() {
        binding.question.visibility = View.VISIBLE
        binding.question.text = getString(R.string.memorise_the_translation)
        binding.nextContainer.visibility = View.VISIBLE
    }

    private fun hideNextViews() {
        binding.question.visibility = View.INVISIBLE
        binding.nextContainer.visibility = View.GONE
    }

    private fun showNoWordsView() {
        binding.noWordsContainer.visibility = View.VISIBLE
    }

    private fun hideNoWordsView() {
        binding.noWordsContainer.visibility = View.GONE
    }

}