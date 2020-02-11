package com.myvocab.myvocab.util

import android.content.Context
import android.os.Build
import android.provider.Settings

fun canStartFastTranslation(context: Context) = canDrawOverlays(context)

fun canDrawOverlays(context: Context?) =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)