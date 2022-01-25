package com.myvocab.core.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class PackageUtils {

    companion object {
        fun isIntentCallable(context: Context, intent: Intent): Boolean {
            val activities = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return activities.size > 0
        }
    }

}