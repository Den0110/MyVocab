package com.myvocab.myvocab.util

import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.myvocab.myvocab.R

fun setupToolbar(toolbar: Toolbar, navController: NavController){
    toolbar.title = navController.currentDestination?.label
    if(!matchDestinations(navController.currentDestination!!, setOf(R.id.vocabFragment, R.id.learningFragment))) {
        toolbar.navigationIcon =
                ContextCompat.getDrawable(toolbar.context, androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener { navController.navigateUp() }
    }
}

private fun matchDestinations(destination: NavDestination, destinationIds: Set<Int>): Boolean {
    var currentDestination: NavDestination? = destination
    do {
        if (destinationIds.contains(currentDestination!!.id)) {
            return true
        }
        currentDestination = currentDestination.parent
    } while (currentDestination != null)
    return false
}