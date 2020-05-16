package com.myvocab.myvocab.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.myvocab.myvocab.R

const val FAST_TRANSLATION_CHANNEL_ID = "fastTranslationServiceChannel"
const val TRANSLATION_CHANNEL_ID = "translationChannel"
const val REMINDER_CHANNEL_ID = "reminderChannel"

fun createFastTranslationNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val serviceChannel = NotificationChannel(
                FAST_TRANSLATION_CHANNEL_ID,
                "Fast translation",
                NotificationManager.IMPORTANCE_MIN
        )

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }
}

fun createTranslationNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val serviceChannel = NotificationChannel(
                TRANSLATION_CHANNEL_ID,
                "Fast Translation",
                NotificationManager.IMPORTANCE_HIGH
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

fun getNotificationIconId() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        R.drawable.ic_notification
    } else {
        R.mipmap.ic_launcher
    }