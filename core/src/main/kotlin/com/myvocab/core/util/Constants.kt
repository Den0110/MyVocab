package com.myvocab.core.util

import java.util.*

object Constants {

    const val MAIN_ACTION = "com.myvocab.myvocab.action.main"
    const val START_FOREGROUND_ACTION = "com.myvocab.myvocab.action.startforeground"
    const val STOP_FOREGROUND_ACTION = "com.myvocab.myvocab.action.stopforeground"

    interface NotificationId {
        companion object {
            const val FOREGROUND_SERVICE = 101
            const val REMINDER = 102
        }
    }

    val REMINDER_DEFAULT_TIME = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 18)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis

}
