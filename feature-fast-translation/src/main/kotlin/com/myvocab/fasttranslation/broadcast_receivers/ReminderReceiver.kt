package com.myvocab.fasttranslation.broadcast_receivers

import android.content.Context
import android.content.Intent
import android.text.Html
import android.text.Spanned
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDeepLinkBuilder
import com.myvocab.core.util.Constants
import com.myvocab.core.util.PreferencesManager
import com.myvocab.core.util.REMINDER_CHANNEL_ID
import com.myvocab.core.util.getNotificationIconId
import com.myvocab.domain.usecases.nextword.GetNextWordToLearnUseCase
import com.myvocab.fasttranslation.R
import dagger.android.DaggerBroadcastReceiver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ReminderReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var getNextWordUseCase: GetNextWordToLearnUseCase

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("New remind")

        val title = "Time to learn new words!"
        val ticker = "Time to learn new words!"

        GlobalScope.launch {
            val text = try {
                val word = getNextWordUseCase.execute(false)
                Html.fromHtml(
                    "Do you know, what does <strong>${word.word.lowercase(Locale.getDefault())}</strong> mean?"
                )
            } catch (e: Exception) {
                if (!preferencesManager.remindOnlyWordsToLearn) {
                    Html.fromHtml("Add new words to your vocab and start learning")
                } else null
            }
            showNotification(context, title, ticker, text)
        }
    }

    private fun showNotification(context: Context, title: String, ticker: String, text: Spanned?) {
        // todo fix
        val pendingIntent = NavDeepLinkBuilder(context)
//                .setComponentName(MainActivity::class.java)
//                .setGraph(R.navigation.navigation_graph)
//                .setDestination(R.id.navigation_learning)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(ticker)
            .setContentText(text)
            .setLargeIcon(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)?.toBitmap())
            .setSmallIcon(getNotificationIconId())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(Constants.NotificationId.REMINDER, notification)
    }

}