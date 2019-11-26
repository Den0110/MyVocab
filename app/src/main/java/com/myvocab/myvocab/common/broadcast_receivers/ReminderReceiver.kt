package com.myvocab.myvocab.common.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDeepLinkBuilder
import com.myvocab.myvocab.R
import com.myvocab.myvocab.ui.MainActivity
import com.myvocab.myvocab.util.Constants
import com.myvocab.myvocab.util.REMINDER_CHANNEL_ID

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "New remind")

        val pendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.navigation_graph)
                .setDestination(R.id.learningFragment)
                .createPendingIntent()

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setContentTitle("Time to learn new words!")
                .setTicker("Time to learn new words!")
                .setContentText("Learn new words from your vocab")
                .setLargeIcon(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)?.toBitmap())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(context).notify(Constants.NotificationId.REMINDER, notification)

    }

}