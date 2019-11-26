package com.myvocab.myvocab.util

import android.content.Context
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

fun Context.dpToPixels(dpSize: Int) = convertDpToPixels(this, dpSize)

fun Fragment.dpToPixels(dpSize: Int) = convertDpToPixels(this.context!!, dpSize)

fun convertDpToPixels(context: Context, dpSize: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return (dpSize * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat().roundToInt()
}