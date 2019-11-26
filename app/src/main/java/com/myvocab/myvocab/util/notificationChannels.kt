package com.myvocab.myvocab.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

const val FAST_TRANSLATION_CHANNEL_ID = "fastTranslationServiceChannel"
const val REMINDER_CHANNEL_ID = "reminderChannel"

fun createFastTranslationNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val serviceChannel = NotificationChannel(
                FAST_TRANSLATION_CHANNEL_ID,
                "Fast translation",
                NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }
}

fun createReminderNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val serviceChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }
}