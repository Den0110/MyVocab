package com.myvocab.core.system

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.*
import androidx.core.content.ContextCompat

class ResourceManager(private val app: Application) {

    val resources = app.resources

    fun getString(@StringRes resId: Int): String = app.getString(resId)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String = app.getString(resId, *formatArgs)

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String =
        resources.getQuantityString(resId, quantity, *formatArgs)

    fun getColor(@ColorRes resId: Int): Int = ContextCompat.getColor(app, resId)

    fun getDimension(@DimenRes resId: Int): Float = resources.getDimension(resId)

    fun getDimensionPixelSize(@DimenRes resId: Int): Int = resources.getDimensionPixelSize(resId)

    fun getDrawable(@DrawableRes resId: Int): Drawable? = ContextCompat.getDrawable(app, resId)

    fun getLocale(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0].language
        } else {
            resources.configuration.locale.language
        }
    }
}