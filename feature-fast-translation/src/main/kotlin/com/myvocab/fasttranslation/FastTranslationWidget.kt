package com.myvocab.fasttranslation

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.myvocab.core.util.Constants
import com.myvocab.core.util.TRANSLATION_CHANNEL_ID
import com.myvocab.core.util.getNotificationIconId
import com.myvocab.domain.common.Resource
import com.myvocab.domain.entities.TranslatableText
import com.myvocab.domain.usecases.translate.TranslateUseCaseResult
import com.myvocab.domain.usecases.translate.TranslationSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FastTranslationWidget
@Inject
constructor(
        private val context: Application
) : LifecycleOwner, ViewModelStoreOwner {

    companion object {
        const val ADD_TO_VOCAB_ACTION = "com.myvocab.myvocab.ui.fast_translation.ADD_TO_VOCAB_ACTION"

        const val LIVE_TIME = 30_000L
    }

    private val addToDictReceiver = AddToDictionaryReceiver()

    private val lifecycle: LifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStore: ViewModelStore = ViewModelStore()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: FastTranslationWidgetViewModel

    private var remoteViews: RemoteViews? = null

    private var word = ""
    private var translation = ""
    private var loading = true
    private var error = false
    private var canAddToDictionary = false
    private var alreadyAddedToDictionary = false

    private val destroyHandler = Handler() // todo refactor
    private val destroyer = Runnable {
        Timber.d("Destroy widget by time")
        finish()
    }

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        remoteViews = RemoteViews(context.packageName, R.layout.translation_notification)
    }

    fun start(text: String) {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        viewModel = ViewModelProvider(this, viewModelFactory)[FastTranslationWidgetViewModel::class.java]

        word = text
        translation = ""
        loading = true
        error = false
        canAddToDictionary = false

        updateTranslationNotification()

        context.registerReceiver(addToDictReceiver, IntentFilter(ADD_TO_VOCAB_ACTION))

        viewModel.translate(TranslatableText(text, "en-ru"))
        lifecycle.coroutineScope.launchWhenStarted {
            viewModel.translateResult.collectLatest { translateResult ->
                when (translateResult) {
                    is Resource.Success -> {
                        showTranslateResult(translateResult.data)
                    }
                    is Resource.Error -> {
                        Timber.e(translateResult.error)
                        translation = ""
                        loading = false
                        error = true
                        updateTranslationNotification()
                    }
                    else -> {}
                }
            }
        }

        destroyHandler.removeCallbacks(destroyer)
        destroyHandler.postDelayed(destroyer, LIVE_TIME)
    }

    @SuppressLint("MissingPermission")
    private fun showTranslateResult(translateResult: TranslateUseCaseResult) {
        when (translateResult.source) {
            TranslationSource.DICTIONARY -> {
                val intent = Intent(ADD_TO_VOCAB_ACTION).apply {
                    putExtra("translate_result", translateResult as Parcelable)
                }
                val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                remoteViews?.setOnClickPendingIntent(R.id.add_to_dictionary_btn, pendingIntent)
                alreadyAddedToDictionary = false
                canAddToDictionary = true
            }
            TranslationSource.VOCAB -> {
                remoteViews?.setOnClickPendingIntent(R.id.add_to_dictionary_btn, null)
                alreadyAddedToDictionary = true
                canAddToDictionary = false
            }
            else -> {
                alreadyAddedToDictionary = false
                canAddToDictionary = false
            }
        }

        translation = mutableListOf(translateResult.word.translation)
            .apply { addAll(translateResult.word.synonyms) }
            .joinToString(separator = ", ")
        loading = false
        error = false

        updateTranslationNotification()

        FirebaseAnalytics.getInstance(context).logEvent("translate", Bundle().apply {
            putString("text", translateResult.word.word)
            putInt("length", translateResult.word.word.length)
            putString("source", translateResult.source.name)
            putInt("meanings_size", translateResult.word.meanings.size)
            putInt("synonyms_size", translateResult.word.synonyms.size)
            putInt("examples_size", translateResult.word.examples.size)
        })
    }

    inner class AddToDictionaryReceiver : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("translate_result")) {
                intent.getParcelableExtra<TranslateUseCaseResult>("translate_result")?.let {
                    lifecycleScope.launch {
                        try {
                            viewModel.addToDictionary(it)
                            Toast.makeText(context, "${it.text.text} added to your vocab", Toast.LENGTH_SHORT).show()
                            Timber.d("Destroy widget after adding word to vocab")
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error, word is not added", Toast.LENGTH_SHORT).show()
                        }
                    }

                    FirebaseAnalytics.getInstance(context).logEvent("add_to_dictionary", Bundle().apply {
                        putString("text", it.word.word)
                        putInt("length", it.word.word.length)
                        putString("source", it.source.name)
                    })

                }
            }
        }

    }

    private fun updateTranslationNotification() {
        remoteViews?.setTextViewText(R.id.word, word)
        remoteViews?.setTextViewText(R.id.translation, translation)
        remoteViews?.setTextViewText(R.id.translation_error, if (error) context.getString(R.string.could_not_translate) else "")
        remoteViews?.setViewVisibility(R.id.translation_progress, if (loading) View.VISIBLE else View.GONE)
        remoteViews?.setViewVisibility(R.id.add_to_dictionary_btn,
                if (canAddToDictionary || alreadyAddedToDictionary) View.VISIBLE else View.GONE)

        if(canAddToDictionary) {
            remoteViews?.setImageViewResource(R.id.add_to_dictionary_btn, R.drawable.ic_add_to_dictionary_36dp)
        } else if(alreadyAddedToDictionary){
            remoteViews?.setImageViewResource(R.id.add_to_dictionary_btn, R.drawable.ic_already_added_to_dictionary_36dp)
        }

        val notification = NotificationCompat.Builder(context, TRANSLATION_CHANNEL_ID)
                .setContentTitle(word)
                .setTicker(word)
                .setContentText(translation)
                .setDefaults(0)
                .setCustomContentView(remoteViews)
                .setLargeIcon(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)?.toBitmap())
                .setSmallIcon(getNotificationIconId())
                .setAutoCancel(true)
                .setOngoing(true)
                .setOnlyAlertOnce(translation.isBlank())
                .build()

        NotificationManagerCompat.from(context).notify(Constants.NotificationId.FOREGROUND_SERVICE, notification)
    }

    private fun destroy() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        try {
            context.unregisterReceiver(addToDictReceiver)
        } catch (e: Exception) { }
        destroyHandler.removeCallbacks(destroyer)
    }

    fun finish() {
        destroy()
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun getViewModelStore(): ViewModelStore = viewModelStore

}