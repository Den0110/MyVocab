package com.myvocab.core.util

import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.myvocab.core.R

fun setupToolbar(toolbar: Toolbar, navController: NavController, topLevelDests: Set<Int>){
    toolbar.title = navController.currentDestination?.label
    if(!matchDestinations(navController.currentDestination, topLevelDests)) {
        toolbar.navigationIcon =
                ContextCompat.getDrawable(toolbar.context, R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { navController.navigateUp() }
    } else {
        toolbar.navigationIcon = null
    }
}

private fun matchDestinations(destination: NavDestination?, destinationIds: Set<Int>): Boolean {
    var currentDestination: NavDestination? = destination
    do {
        if (destinationIds.contains(currentDestination?.id)) {
            return true
        }
        currentDestination = currentDestination?.parent
    } while (currentDestination != null)
    return false
}