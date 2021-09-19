package com.myvocab.myvocab.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

fun Context.dpToPixels(dpSize: Int) = convertDpToPixels(this, dpSize)

fun Fragment.dpToPixels(dpSize: Int) = convertDpToPixels(this.context!!, dpSize)

fun Fragment.spToPixels(spSize: Int) = convertSpToPixels(this.context!!, spSize)

fun convertDpToPixels(context: Context, dpSize: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return (dpSize * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat().roundToInt()
}

fun convertSpToPixels(context: Context, spSize: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize.toFloat(), context.resources.displayMetrics).roundToInt()
}