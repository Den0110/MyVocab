package com.myvocab.navigation

import androidx.navigation.NavController

class Navigator {

    var navController: NavController? = null

    fun navigateToFlow(navigationFlow: NavigationFlow) {
        requireNotNull(navController, { "NavController shouldn't be null" })

        val destination = when (navigationFlow) {
            NavigationFlow.Learning -> NavigationGraphDirections.toLeaningFlow()
            NavigationFlow.MyWords -> NavigationGraphDirections.toMyWordsFlow()
            NavigationFlow.WordLists -> NavigationGraphDirections.toWordListsFlow()
            NavigationFlow.Settings -> NavigationGraphDirections.toSettingsFlow()
        }

        navController?.navigate(destination)
    }
}