package com.myvocab.myvocab.ui.fast_translation

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.*
import com.myvocab.myvocab.R
import com.myvocab.myvocab.data.model.TranslatableText
import com.myvocab.myvocab.data.model.TranslateUseCaseResult
import com.myvocab.myvocab.data.model.TranslationSource
import com.myvocab.myvocab.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class FastTranslationWidget
@Inject
constructor(
        private val context: Application
) : LifecycleOwner, ViewModelStoreOwner {

    companion object {
        const val ADD_TO_VOCAB_ACTION = "com.myvocab.myvocab.ui.fast_translation.ADD_TO_VOCAB_ACTION"
    }

    private val addToDictReceiver = AddToDictionaryReceiver()

    private val lifecycle: LifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStore: ViewModelStore = ViewModelStore()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: FastTranslationWidgetViewModel
    private var disposables: CompositeDisposable = CompositeDisposable()

    private var remoteViews: RemoteViews? = null

    private var word = ""
    private var translation = ""
    private var loading = true
    private var error = false
    private var canAddToDictionary = false

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        remoteViews = RemoteViews(context.packageName, R.layout.translation_notification)
    }

    fun start(text: String) {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FastTranslationWidgetViewModel::class.java)

        word = text
        translation = ""
        loading = true
        error = false
        canAddToDictionary = false

        updateTranslationNotification()

        context.registerReceiver(addToDictReceiver, IntentFilter(ADD_TO_VOCAB_ACTION))

        val translatableText = TranslatableText(text, "en-ru")
        val translateDisposable = viewModel
                .translate(translatableText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ translateResult ->
                    if (translateResult.source == TranslationSource.DICTIONARY) {
                        val intent = Intent(ADD_TO_VOCAB_ACTION).apply {
                            putExtra("translate_result", translateResult)
                        }
                        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        remoteViews?.setOnClickPendingIntent(R.id.add_to_dictionary_btn, pendingIntent)
                        canAddToDictionary = true
                    } else {
                        canAddToDictionary = false
                    }

                    translation = translateResult.translations.joinToString(separator = ", ", limit = 2)
                    loading = false
                    error = false

                    updateTranslationNotification()

                }, {
                    Timber.e(it)
                    translation = ""
                    loading = false
                    error = true
                    updateTranslationNotification()
                })

        disposables.add(translateDisposable)
    }

    inner class AddToDictionaryReceiver : BroadcastReceiver() {

        private val compositeDisposable = CompositeDisposable()

        override fun onReceive(context: Context, intent: Intent) {
            compositeDisposable.clear()
            if (intent.hasExtra("translate_result")) {
                intent.getParcelableExtra<TranslateUseCaseResult>("translate_result")?.let {
                    val addToDictionaryDisposable =
                            viewModel
                                    .addToDictionary(it)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        canAddToDictionary = false
                                        updateTranslationNotification()
                                        Toast.makeText(context, "${it.text.text} added to your vocab", Toast.LENGTH_SHORT).show()
                                    }, {
                                        Toast.makeText(context, "Error, word is not added", Toast.LENGTH_SHORT).show()
                                    })

                    compositeDisposable.add(addToDictionaryDisposable)
                }
            }
        }

    }

    private fun updateTranslationNotification() {
        remoteViews?.setTextViewText(R.id.word, word)
        remoteViews?.setTextViewText(R.id.translation, translation)
        remoteViews?.setTextViewText(R.id.translation_error, if (error) "Unable to translate" else "")
        remoteViews?.setViewVisibility(R.id.translation_progress, if (loading) View.VISIBLE else View.GONE)
        remoteViews?.setViewVisibility(R.id.add_to_dictionary_btn, if (canAddToDictionary) View.VISIBLE else View.GONE)

        val notification = NotificationCompat.Builder(context, TRANSLATION_CHANNEL_ID)
                .setContentTitle(word)
                .setTicker(word)
                .setContentText(translation)
                .setDefaults(0)
                .setCustomContentView(remoteViews)
//                .addAction(0, "Add to vocab", pendingIntent)
                .setLargeIcon(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)?.toBitmap())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setOngoing(true)
                .build()

        NotificationManagerCompat.from(context).notify(Constants.NotificationId.FOREGROUND_SERVICE, notification)
    }

    private fun destroy() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        context.unregisterReceiver(addToDictReceiver)
        disposables.clear()
    }

    fun finish() {
        destroy()
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun getViewModelStore(): ViewModelStore = viewModelStore

}