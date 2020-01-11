package com.myvocab.myvocab.common.broadcast_receivers

import android.content.Context
import android.content.Intent
import android.text.Html
import android.text.Spanned
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDeepLinkBuilder
import com.myvocab.myvocab.R
import com.myvocab.myvocab.domain.learning.GetNextWordToLearnUseCase
import com.myvocab.myvocab.ui.MainActivity
import com.myvocab.myvocab.util.Constants
import com.myvocab.myvocab.util.REMINDER_CHANNEL_ID
import dagger.android.DaggerBroadcastReceiver
import timber.log.Timber
import javax.inject.Inject

class ReminderReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var getNextWordUseCase: GetNextWordToLearnUseCase

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("New remind")

        val title = "Time to learn new words!"
        val ticker = "Time to learn new words!"
        var text: Spanned

        var getWordDisposable = getNextWordUseCase.execute(false).subscribe({
            text = Html.fromHtml("Do you know, what does <strong>${it.word}</strong> mean?")
            showNotification(context, title, ticker, text)
        },{
            text = Html.fromHtml("Add new words to your vocab and start learning")
            showNotification(context, title, ticker, text)
        })

    }

    private fun showNotification(context: Context, title: String, ticker: String, text: Spanned) {
        val pendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.navigation_graph)
                .setDestination(R.id.navigation_learning)
                .createPendingIntent()

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(ticker)
                .setContentText(text)
                .setLargeIcon(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)?.toBitmap())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(context).notify(Constants.NotificationId.REMINDER, notification)
    }

}