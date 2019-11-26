package com.myvocab.myvocab.util

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

}
