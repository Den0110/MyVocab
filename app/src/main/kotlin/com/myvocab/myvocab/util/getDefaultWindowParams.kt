package com.myvocab.myvocab.util

import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager

@Suppress("DEPRECATION")
fun getDefaultWindowParams(width: Int, height: Int): WindowManager.LayoutParams {
    return WindowManager.LayoutParams(
            width,
            height,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
    )
}
