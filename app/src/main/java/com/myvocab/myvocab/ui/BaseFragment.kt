package com.myvocab.myvocab.ui

import com.myvocab.myvocab.util.hideKeyboard
import dagger.android.support.DaggerFragment

open class BaseFragment : DaggerFragment() {

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.let { hideKeyboard(it) }
    }

}