package com.myvocab.myvocab.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.animation.Animation
import androidx.transition.Transition
import io.reactivex.CompletableEmitter

open class AnimationListenerAdapter : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {}
    override fun onAnimationRepeat(animation: Animation) {}
}

fun rxTransitionCallback(emitter: CompletableEmitter) =
        object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                emitter.onComplete()
            }

            override fun onTransitionResume(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {
                emitter.onComplete()
            }

            override fun onTransitionCancel(transition: Transition) {
                emitter.onComplete()
            }

            override fun onTransitionStart(transition: Transition) {}
        }

fun rxAnimatorCallback(emitter: CompletableEmitter) =
        object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                emitter.onComplete()
            }

            override fun onAnimationCancel(animation: Animator?) {
                emitter.onComplete()
            }

            override fun onAnimationPause(animation: Animator?) {
                emitter.onComplete()
            }
        }

fun enableSelectItemBg(vararg views: View) {
    views.forEach {
        setSelectItemBg(it, getSelectableItemBg(it.context))
    }
}

fun disableSelectItemBg(vararg views: View) {
    views.forEach {
        setSelectItemBg(it, null)
    }
}

fun getSelectableItemBg(context: Context): Drawable? {
    val attribute = intArrayOf(android.R.attr.selectableItemBackground)
    val array = context.theme?.obtainStyledAttributes(attribute)
    val drawable = array?.getDrawable(0)
    array?.recycle()
    return drawable
}

private fun setSelectItemBg(view: View, bg: Drawable?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        view.foreground = bg
    else
        view.background = bg

}
