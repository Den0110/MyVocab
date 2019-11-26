package com.myvocab.myvocab.util

import android.view.animation.Animation

open class AnimationListenerAdapter : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {}
    override fun onAnimationRepeat(animation: Animation) {}
}
