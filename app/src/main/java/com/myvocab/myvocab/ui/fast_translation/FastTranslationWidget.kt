package com.myvocab.myvocab.ui.fast_translation

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.*
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.util.AnimationListenerAdapter
import com.myvocab.myvocab.util.getDefaultWindowParams
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FastTranslationWidget
@Inject
constructor(
        private val context: Application,
        private val windowManager: WindowManager,
        private val inflater: LayoutInflater
) : LifecycleOwner, ViewModelStoreOwner {

    companion object {
        const val TAG = "FastTranslationWidget"
    }

    private val lifecycle: LifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStore: ViewModelStore = ViewModelStore()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: FastTranslationWidgetViewModel
    private var disposables: CompositeDisposable = CompositeDisposable()

    private var translationRootView: View? = null

    private var translationBgView: View? = null
    private var translationCardView: CardView? = null
    private var translatableTextView: TextView? = null
    private var translatedTextView: TextView? = null
    private var translationProgressBar: ProgressBar? = null
    private var addToDictionaryBtn: ImageView? = null

    private var translationBgShowAnim: Animation? = null
    private var translationBgHideAnim: Animation? = null
    private var translationCardShowAnim: Animation? = null
    private var translationCardHideAnim: Animation? = null

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        setupAnimations()
        setupViews()
    }

    fun start(text: String) {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FastTranslationWidgetViewModel::class.java)

        translatableTextView!!.text = text
        translatedTextView!!.text = ""
        translatedTextView!!.visibility = View.GONE
        translationProgressBar!!.visibility = View.VISIBLE

        val translatableText = TranslatableText(text, "ru")
        val translateDisposable = viewModel
                .translate(translatableText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ translatedText ->
            if (translatedText.translatable?.text?.contains(' ') != true) {
                addToDictionaryBtn!!.visibility = View.VISIBLE
                addToDictionaryBtn!!.setOnClickListener {
                    viewModel.addToDictionary(translatedText)
                    destroy()
                }
            } else {
                addToDictionaryBtn!!.visibility = View.GONE
                addToDictionaryBtn!!.setOnClickListener(null)
            }
            translatedTextView!!.text = translatedText.data?.translations?.get(0)?.translatedText
            translatedTextView!!.visibility = View.VISIBLE
            translationProgressBar!!.visibility = View.GONE
        }, {
            Log.e(TAG, it.toString())
            Toast.makeText(context, R.string.could_not_translate, Toast.LENGTH_SHORT).show()
            translationProgressBar!!.visibility = View.GONE
        })
        disposables.add(translateDisposable)

        showTranslationView()
    }

    @SuppressLint("InflateParams")
    private fun setupViews() {
        translationRootView = inflater.inflate(R.layout.fast_translation_main_view, null)

        val translationViewParams = getDefaultWindowParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )

        windowManager.addView(translationRootView, translationViewParams)

        translationBgView = translationRootView!!.findViewById(R.id.translation_view_bg)
        translationCardView = translationRootView!!.findViewById(R.id.translation_view_card)
        translatableTextView = translationRootView!!.findViewById(R.id.translatable_text_view)
        translatedTextView = translationRootView!!.findViewById(R.id.translated_text_view)
        translationProgressBar = translationRootView!!.findViewById(R.id.translation_progress_bar)
        addToDictionaryBtn = translationRootView!!.findViewById(R.id.add_to_dictionary)
    }

    private fun cleanViews() {
        if (translationRootView != null) {
            windowManager.removeView(translationRootView)
            translationRootView = null
        }
    }

    private fun setupAnimations() {
        translationBgShowAnim = AnimationUtils.loadAnimation(context, R.anim.translation_bg_show)
        translationBgHideAnim = AnimationUtils.loadAnimation(context, R.anim.translation_bg_hide)

        translationCardShowAnim = AnimationUtils.loadAnimation(context, R.anim.translation_card_show)
        translationCardShowAnim!!.setAnimationListener(object : AnimationListenerAdapter() {
            override fun onAnimationEnd(animation: Animation) {
                translationBgView!!.setOnClickListener { destroy() }
            }
        })
        translationCardHideAnim = AnimationUtils.loadAnimation(context, R.anim.translation_card_hide)
        translationCardHideAnim!!.setAnimationListener(object : AnimationListenerAdapter() {
            override fun onAnimationEnd(animation: Animation) {
                translationRootView!!.visibility = View.GONE
            }
        })
    }

    private fun showTranslationView() {
        translationRootView!!.visibility = View.VISIBLE
        translationBgView!!.startAnimation(translationBgShowAnim)
        translationCardView!!.startAnimation(translationCardShowAnim)
    }

    private fun hideTranslationView() {
        translationBgView!!.startAnimation(translationBgHideAnim)
        translationCardView!!.startAnimation(translationCardHideAnim)
    }

    private fun destroy() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        hideTranslationView()
        viewModelStore.clear()
        disposables.clear()
    }

    fun finish() {
        destroy()
        cleanViews()
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun getViewModelStore(): ViewModelStore = viewModelStore

}