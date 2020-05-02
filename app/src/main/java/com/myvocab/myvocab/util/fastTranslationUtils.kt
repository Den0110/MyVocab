package com.myvocab.myvocab.util

import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

const val REQUEST_CODE_DRAW_OVERLAYS = 1
const val REQUEST_CODE_BATTERY = 2

fun canDrawOverlays(context: Context?) =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)

fun ignoresPowerOptimization(context: Context?) =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (context!!.getSystemService(Context.POWER_SERVICE) as PowerManager)
                        .isIgnoringBatteryOptimizations(context.packageName)